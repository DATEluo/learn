    package com.example.j2ee_new.controller;

    import com.example.j2ee_new.entity.Book;
    import com.example.j2ee_new.entity.User;
    import com.example.j2ee_new.services.BookService;
    import com.example.j2ee_new.services.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import javax.servlet.http.HttpServletRequest;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.*;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/books")
    public class BookController {
        @Autowired
        private BookService bookService;

        @Autowired
        private UserService userService;

        // 新书推荐
        @GetMapping("/new")
        public List<Book> getNewBooks(
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int pageSize) {
            return bookService.findNewBooks(page, pageSize);
        }

        // 图书借阅
        @PostMapping("/borrow")
        public ResponseEntity<?> borrowBook(
                @RequestBody Map<String, Object> payload,
                HttpServletRequest request) {
            try {
                Integer bookId = (Integer) payload.get("bookId");

                // 日期处理
                Object returnTimeObj = payload.get("returnTime");
                Date returnTime;

                if (returnTimeObj instanceof String) {
                    // 解析ISO 8601字符串
                    String dateStr = (String) returnTimeObj;
                    returnTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .parse(dateStr);
                } else if (returnTimeObj instanceof Long) {
                    // 处理时间戳
                    returnTime = new Date((Long) returnTimeObj);
                } else {
                    return ResponseEntity.badRequest().body("Invalid date format");
                }

                String email = (String) request.getAttribute("email");
                User user = userService.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.badRequest().body("User not found");
                }

                bookService.borrowBook(bookId, user.getId(), returnTime);
                return ResponseEntity.ok("Borrow successful");

            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Invalid date format");
            }
        }

        // 图书管理-查询
        @GetMapping("")
        public List<Book> searchBooks(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String author,
                @RequestParam(required = false) String press,
                @RequestParam(required = false) String status,
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int pageSize) {
            return bookService.searchBooks(name, author, press, status, page, pageSize);
        }
        // 图书管理-新增
        @PostMapping("")
        public ResponseEntity<?> addBook(@RequestBody Book book, HttpServletRequest request) {
            String role = (String) request.getAttribute("role");
            String email = (String) request.getAttribute("email");

            System.out.println("=== ADD BOOK REQUEST ===");
            System.out.println("Email: " + email);
            System.out.println("Role: " + role);
            System.out.println("Role type: " + (role != null ? role.getClass().getName() : "null"));
            System.out.println("Expected ADMIN? " + "ADMIN".equals(role));
            System.out.println("=======================");

            if (!"ADMIN".equals(request.getAttribute("role"))) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            bookService.addBook(book);
            return ResponseEntity.ok("Book added");
        }

        // 图书管理-编辑
        @PutMapping("/{id}")
        public ResponseEntity<?> updateBook(
                @PathVariable Integer id,
                @RequestBody Book book,
                HttpServletRequest request) {
            if (!"ADMIN".equals(request.getAttribute("role"))) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            book.setId(id);
            bookService.updateBook(book);
            return ResponseEntity.ok("Book updated");
        }

        // 当前借阅-查询
        @GetMapping("/current")
        public List<Book> getCurrentBorrows(
                HttpServletRequest request,
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int pageSize) {
            String role = (String) request.getAttribute("role");
            String email = (String) request.getAttribute("email");

            // 根据email获取当前用户ID
            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                return Collections.emptyList();
            }
            Integer currentUserId = currentUser.getId();

            if ("ADMIN".equals(role)) {
                // 管理员查看所有未归还图书（状态为1和2）
                return bookService.searchBooks(null, null, null, "1,2", page, pageSize);
            } else {
                // 普通用户查看自己的借阅（状态为1和2）
                return bookService.searchBooks(null, null, null, "1,2", page, pageSize)
                        .stream()
                        .filter(b -> currentUserId.equals(b.getBorrower()))
                        .collect(Collectors.toList());
            }
        }

        // 归还图书
        @PostMapping("/return/{id}")
        public ResponseEntity<?> returnBook(
                @PathVariable Integer id,
                @RequestParam(required = false) boolean confirm,
                HttpServletRequest request) {
            String role = (String) request.getAttribute("role");
            String email = (String) request.getAttribute("email");

            // 根据email获取当前用户ID
            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not found");
            }
            Integer currentUserId = currentUser.getId();

            Book book = bookService.findById(id);
            if (book == null) {
                return ResponseEntity.badRequest().body("Book not found");
            }

            // 权限检查
            if (confirm) {
                if (!"ADMIN".equals(role)) {
                    return ResponseEntity.status(403).body("Forbidden");
                } else {
                    // 管理员确认归还
                    bookService.returnBook(id, true);
                }
            } else {
                // 用户申请归还：必须是借阅者本人
                if (!currentUserId.equals(book.getBorrower())) {
                    return ResponseEntity.status(403).body("Forbidden");
                } else {
                    // 用户确认归还
                    bookService.returnBook(id, false);
                }
            }

            bookService.returnBook(id, confirm);
            return ResponseEntity.ok("Return processed");
        }
    }