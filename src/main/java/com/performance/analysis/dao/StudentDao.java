package com.performance.analysis.dao;

import com.performance.analysis.pojo.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:49
 * <p>
 * 学生信息数据访问
 */
@Mapper
@Repository
public interface StudentDao {

    @Insert("insert into student(stu_no,name,grade) values(#{stuNo},#{name},#{grade})")
    void addStudent(String stuNo, String name, Integer grade);

    @Select("select * from student")
    List<Student> findAllStudent();

    @Select("select * from student where grade = #{grade}")
    List<Student> findStudentByGrade(Integer grade);
}
