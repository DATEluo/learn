// BookMapper.java
package com.example.j2ee_new.mapper;

import com.example.j2ee_new.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM book WHERE book_id = #{id}")
    @Results(id = "bookMap", value = {
            @Result(id = true, column = "book_id", property = "id"),
            @Result(column = "book_name", property = "name"),
            @Result(column = "book_isbn", property = "isbn"),
            @Result(column = "book_press", property = "press"),
            @Result(column = "book_author", property = "author"),
            @Result(column = "book_pagination", property = "pagination"),
            @Result(column = "book_price", property = "price"),
            @Result(column = "book_uploadtime", property = "uploadTime"),
            @Result(column = "book_status", property = "status"),
            @Result(column = "book_borrower", property = "borrower"),
            @Result(column = "book_borrowtime", property = "borrowTime"),
            @Result(column = "book_returntime", property = "returnTime")
    })
    Book findById(Integer id);

    @Update("UPDATE book SET " +
            "book_name=#{name}, " +
            "book_isbn=#{isbn}, " +
            "book_press=#{press}, " +
            "book_author=#{author}, " +
            "book_pagination=#{pagination}, " +
            "book_price=#{price}, " +
            "book_status=#{status}, " +  // 新增状态更新
            "book_borrower=#{borrower}, " +
            "book_borrowtime=#{borrowTime}, " +
            "book_returntime=#{returnTime} " +
            "WHERE book_id=#{id}")
    void update(Book book);

    @Select("<script>" +
            "SELECT * FROM book " +
            "WHERE 1=1 " +
            "<if test='name != null'> AND book_name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='author != null'> AND book_author LIKE CONCAT('%', #{author}, '%')</if>" +
            "<if test='press != null'> AND book_press LIKE CONCAT('%', #{press}, '%')</if>" +
            "<if test='statusList != null'> " +
            "   AND book_status IN " +
            "   <foreach item='s' collection='statusList' open='(' separator=',' close=')'>" +
            "       #{s}" +
            "   </foreach>" +
            "</if>" +
            "ORDER BY book_uploadtime DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @ResultMap("bookMap")
    List<Book> findAll(@Param("name") String name,
                       @Param("author") String author,
                       @Param("press") String press,
                       @Param("statusList") List<String> statusList,
                       @Param("offset") int offset,
                       @Param("pageSize") int pageSize);

    @Insert("INSERT INTO book (book_name, book_isbn, book_press, book_author, " +
            "book_pagination, book_price, book_uploadtime, book_status) " +
            "VALUES (#{name}, #{isbn}, #{press}, #{author}, #{pagination}, " +
            "#{price}, #{uploadTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Book book);
}