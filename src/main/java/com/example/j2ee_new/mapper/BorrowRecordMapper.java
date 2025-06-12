package com.example.j2ee_new.mapper;

import com.example.j2ee_new.entity.BorrowRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    @Insert("INSERT INTO borrow_record (record_id, record_bookname, record_bookisbn, record_borrower, record_borrowtime, record_remandtime) " +
            "VALUES (#{id}, #{bookName}, #{bookIsbn}, #{borrower}, #{borrowTime}, #{remandTime})")
    void insert(BorrowRecord record);

    @Select("<script>" +
            "SELECT * FROM borrow_record " +
            "WHERE 1=1 " +
            "<if test='borrower != null and borrower != \"\"'> AND record_borrower = #{borrower}</if>" +
            "<if test='bookName != null and bookName != \"\"'> AND record_bookname LIKE CONCAT('%', #{bookName}, '%')</if>" +
            "ORDER BY record_borrowtime DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @Results(id = "recordMap", value = {
            @Result(id = true, column = "record_id", property = "id"),
            @Result(column = "record_bookname", property = "bookName"),
            @Result(column = "record_bookisbn", property = "bookIsbn"),
            @Result(column = "record_borrower", property = "borrower"),
            @Result(column = "record_borrowtime", property = "borrowTime"),
            @Result(column = "record_remandtime", property = "remandTime")
    })
    List<BorrowRecord> findAll(@Param("borrower") String borrower,
                               @Param("bookName") String bookName,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);
}