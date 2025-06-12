package com.example.j2ee_new.services;


import com.example.j2ee_new.entity.User;
import com.example.j2ee_new.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User login(User user) {
        return userMapper.login(user);
    }

    public  User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public User findById(Integer borrower) {
        return userMapper.findById(borrower);
    }
}
