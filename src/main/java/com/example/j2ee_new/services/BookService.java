package com.example.j2ee_new.services;

import com.example.j2ee_new.entity.Book;
import com.example.j2ee_new.entity.BorrowRecord;
import com.example.j2ee_new.entity.User;
import com.example.j2ee_new.mapper.BookMapper;
import com.example.j2ee_new.mapper.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserService userService;
    public Book findById(Integer id) {
        return bookMapper.findById(id);
    }

    public List<Book> findNewBooks(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return bookMapper.findAll(null, null, null, Collections.singletonList("0"), offset, pageSize);
    }

    public List<Book> searchBooks(String name, String author, String press, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<String> statusList = null;

        if (status != null && !status.isEmpty()) {
            // 将逗号分隔的状态字符串转换为列表
            statusList = Arrays.asList(status.split(","));
        }

        // 调用修改后的findAll方法，添加作者和出版社参数
        return bookMapper.findAll(name, author, press, statusList, offset, pageSize);
    }

    public List<BorrowRecord> findRecords(String borrower, String bookName, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return borrowRecordMapper.findAll(borrower, bookName, offset, pageSize);
    }

    @Transactional
    public void borrowBook(Integer bookId, Integer borrowerId, Date returnTime) {
        Book book = bookMapper.findById(bookId);
        if (book != null && "0".equals(book.getStatus())) {
            book.setStatus("1");
            book.setBorrower(borrowerId);
            book.setBorrowTime(new Date());
            book.setReturnTime(returnTime);
            bookMapper.update(book);
        }
    }

    // 在returnBook方法中添加记录生成逻辑
    @Transactional
    public void returnBook(Integer bookId, boolean isAdminConfirm) {
        Book book = bookMapper.findById(bookId);
        if (book != null && ("1".equals(book.getStatus()) || "2".equals(book.getStatus()))) {
            if (isAdminConfirm) {
                // 管理员确认归还 - 生成借阅记录
                BorrowRecord record = new BorrowRecord();
                record.setId(UUID.randomUUID().toString().replace("-", ""));
                record.setBookName(book.getName());
                record.setBookIsbn(book.getIsbn());

                // 获取借阅人姓名
                User borrowerUser = userService.findById(book.getBorrower());
                if (borrowerUser != null) {
                    record.setBorrower(borrowerUser.getName());
                } else {
                    record.setBorrower("Unknown");
                }

                record.setBorrowTime(book.getBorrowTime());
                record.setRemandTime(new Date());
                borrowRecordMapper.insert(record);

                // 清除借阅信息
                book.setStatus("0");
                book.setBorrower(null);
                book.setBorrowTime(null);
                book.setReturnTime(null);
            } else {
                // 用户申请归还
                book.setStatus("2");
            }
            bookMapper.update(book);
        }
    }

    public void addBook(Book book) {
        book.setUploadTime(new Date());
        book.setStatus("0");
        bookMapper.insert(book);
    }

    public void updateBook(Book book) {
        Book existing = bookMapper.findById(book.getId());
        if (existing != null &&
                "0".equals(existing.getStatus()) ||
                "3".equals(existing.getStatus())) {
            bookMapper.update(book);
        }
    }
}