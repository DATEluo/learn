package com.example.j2ee_new.controller;

import com.example.j2ee_new.entity.BorrowRecord;
import com.example.j2ee_new.entity.User;
import com.example.j2ee_new.services.BookService;
import com.example.j2ee_new.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordController {
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<BorrowRecord> getRecords(
            @RequestParam(required = false) String borrower,
            @RequestParam(required = false) String bookName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        String email = (String) request.getAttribute("email");

        // 普通用户只能查自己
        if (!"ADMIN".equals(role)) {
            User user = userService.findByEmail(email);
            if (user != null) {
                borrower = user.getName(); // 使用用户名查询
            } else {
                return Collections.emptyList();
            }
        }

        return bookService.findRecords(borrower, bookName, page, pageSize);
    }
}