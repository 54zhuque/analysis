package com.performance.analysis.dao;

import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.pojo.StudentEvaluationResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午2:43
 */
@Mapper
@Repository
public interface StudentEvaluationDao {

    /**
     * 查询对应年级专业下的学生考核信息
     *
     * @param majorGrade
     * @return
     */
    @Select("select " +
            "student.stu_no," +
            "student.name as stu_name," +
            "physical_evaluation.fix_score as physical_score," +
            "moral_evaluation.fix_score as moral_score," +
            "major_evaluation.fix_score as major_score," +
            "english_evaluation.english_score as english_score " +
            "from " +
            "(((student " +
            "left join physical_evaluation on student.stu_no = physical_evaluation.stu_no) " +
            "left join moral_evaluation on student.stu_no = moral_evaluation.stu_no) " +
            "left join major_evaluation on student.stu_no = major_evaluation.stu_no) " +
            "left join english_evaluation on student.stu_no = english_evaluation.stu_no " +
            "where student.stu_no like '%${majorGrade}%'")
    List<StudentEvaluationDto> findStudentEvaluations(@Param("majorGrade") String majorGrade);

    /**
     * 存储学生评优记录
     *
     * @param studentEvaluationResult
     */
    @Insert("insert into student_evaluation(stu_no,stu_name,physical_score,moral_score,major_score,english_score,fix_score,evaluation_result) " +
            "values(#{stuNo},#{stuName},#{physicalScore},#{moralScore},#{majorScore},#{englishScore},#{fixScore},#{evaluationResult})")
    void addStudentEvaluationResult(StudentEvaluationResult studentEvaluationResult);

    /**
     * 查询对应年级专业下的评优信息
     *
     * @param majorGrade
     * @return
     */
    @Select("select * from student_evaluation where evaluation_result = #{evaluationResult} and stu_no like '%${majorGrade}%' order by fix_score asc")
    List<StudentEvaluationResult> findStudentEvaluationByMajorGrade(@Param("evaluationResult") String evaluationResult, @Param("majorGrade") String majorGrade);
}
