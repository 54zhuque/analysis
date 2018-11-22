package com.performance.analysis.dao;

import com.performance.analysis.dto.StudentEvaluationDto;
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
    @Insert("insert or replace into student(stu_no,name,grade,major) values(#{stuNo},#{name},#{grade},#{major})")
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

    /**
     * 学生成绩概览
     */
    @Select("select " +
            "student.stu_no," +
            "student.major," +
            "student.name as stu_name," +
            "student.grade as stu_grade," +
            "physical_evaluation.fix_score as physical_score," +
            "moral_evaluation.fix_score as moral_score," +
            "major_evaluation.fix_score as major_score," +
            "english_evaluation.english_score as english_score," +
            "extra_evaluation.extra_score as extra_score " +
            "from " +
            "(((student " +
            "left join physical_evaluation on student.stu_no = physical_evaluation.stu_no) " +
            "left join moral_evaluation on student.stu_no = moral_evaluation.stu_no) " +
            "left join major_evaluation on student.stu_no = major_evaluation.stu_no) " +
            "left join english_evaluation on student.stu_no = english_evaluation.stu_no " +
            "left join extra_evaluation on student.stu_no = extra_evaluation.stu_no "
    )
    List<StudentEvaluationDto> studentsResultOverview();

    /**
     * 根据学号查询学生信息
     * @param stuNo
     * @return
     */
    @Select("select * from student where stu_no = #{stuNo}")
    Student findStudentByStuNo(String stuNo);

}
