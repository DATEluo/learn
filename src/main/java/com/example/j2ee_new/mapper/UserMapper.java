package com.example.j2ee_new.mapper;

import com.example.j2ee_new.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper{
    @Select("select * from user where user_email=#{email} AND " +
            "user_password=#{password} AND user_status!='1'")
    @Results(id = "userMap", value = {
            //id字段默认为false，表示不是主键
            //column 表示数据库表字段，property 表示持久化类属性名称
            @Result(id = true,column = "user_id",property = "id"),
            @Result(column = "user_username",property = "name"),
            @Result(column = "user_password",property = "password"),
            @Result(column = "user_email",property = "email"),
            @Result(column = "user_role",property = "role"),
            @Result(column = "user_status",property = "status"),
    })
    User login(User user);

    @Select("SELECT * FROM user WHERE user_email = #{email}")
    @ResultMap("userMap")
    User findByEmail(String email);

    @Select("SELECT * FROM user WHERE user_id = #{borrower}")
    @ResultMap("userMap")
    User findById(Integer borrower);
}