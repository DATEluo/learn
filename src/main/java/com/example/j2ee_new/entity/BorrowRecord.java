package com.example.j2ee_new.entity;

import java.io.Serializable;
import java.util.Date;

public class BorrowRecord implements Serializable {
    private String id;             // 借阅记录ID
    private String bookName;       // 借阅的图书名称
    private String bookIsbn;       // 借阅的图书的ISBN编号
    private String borrower;       // 图书借阅人
    private Date borrowTime;       // 图书借阅时间
    private Date remandTime;       // 图书归还时间

    public BorrowRecord() {
    }

    public BorrowRecord(String id, String bookName, String bookIsbn, String borrower, Date borrowTime, Date remandTime) {
        this.id = id;
        this.bookName = bookName;
        this.bookIsbn = bookIsbn;
        this.borrower = borrower;
        this.borrowTime = borrowTime;
        this.remandTime = remandTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Date getRemandTime() {
        return remandTime;
    }

    public void setRemandTime(Date remandTime) {
        this.remandTime = remandTime;
    }
}