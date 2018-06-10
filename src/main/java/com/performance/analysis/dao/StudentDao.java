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

    /**
     * 添加学生
     *
     * @param student
     */
    @Insert("insert or ignore into student(stu_no,name,grade,major) values(#{stuNo},#{name},#{grade},#{major})")
    void addStudent(Student student);

    /**
     * 查询所有学生信息
     *
     * @return
     */
    @Select("select * from student")
    List<Student> findAllStudent();

    /**
     * 查询某个年级学生信息
     *
     * @param grade
     * @return
     */
    @Select("select * from student where grade = #{grade}")
    List<Student> findStudentByGrade(Integer grade);
}
