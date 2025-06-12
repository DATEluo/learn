//package com.example.j2ee_new;
//
//import com.example.j2ee_new.controller.UserController;
//import com.example.j2ee_new.entity.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//
//@SpringBootTest
//class UserControllerTest {
//
//    @Autowired
//    private UserController userController;
//
//    @Test
//    void testLogin() {
//        User user = new User();
//        user.setEmail("weqweq@qq.com");
//        user.setPassword("123456");
//        ResponseEntity<?> response = userController.login(user);
//        System.out.println("测试结果: " + response.getStatusCode());
//    }
//}