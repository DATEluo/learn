package com.example.j2ee_new.controller;

import com.example.j2ee_new.entity.User;
import com.example.j2ee_new.services.UserService;
import com.example.j2ee_new.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loggedInUser = userService.login(user);
        if (loggedInUser != null) {
            String token = jwtUtil.generateToken(
                    loggedInUser.getEmail(),
                    loggedInUser.getRole().toUpperCase());
            Map<String, Object> response = new HashMap<>();
            response.put("user", loggedInUser);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 前端需清除token
        return ResponseEntity.ok("Logout successful");
    }
}