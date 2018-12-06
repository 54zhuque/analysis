package com.performance.analysis.dao;

import com.performance.analysis.pojo.ScholarshipEvaluatingResult;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 奖学金评选过程
 *
 * @author tangwei
 * @since 1.0
 */
@Mapper
@Repository
public interface ScholarshipEvaluatingDao {
    /**
     * 新增或修改
     *
     * @param result
     */
    @Insert("insert or replace into scholarship_evaluating(stu_no,stu_name,stu_grade,physical_score,moral_score,major_score,english_score,fix_score,evaluation_result1,evaluation_result2,extra_score,stu_major,basic_score) " +
            "values(#{stuNo},#{stuName},#{stuGrade},#{physicalScore},#{moralScore},#{majorScore},#{englishScore},#{fixScore},#{evaluationResult1},#{evaluationResult2},#{extraScore},#{stuMajor},#{basicScore})")
    void saveOrUpdateScholarshipEvaluatingResult(ScholarshipEvaluatingResult result);

    /**
     * 更新评选过程result
     *
     * @param evaluation_result2
     * @param stuNo
     */
    @Update("update scholarship_evaluating set evaluation_result2 = #{evaluation_result2} where stu_no = #{stuNo}")
    void updateScholarshipEvaluatingResult(@Param("evaluation_result2") String evaluation_result2, @Param("stuNo") String stuNo);

    /**
     * 通过学号查询评选过程
     *
     * @param stuNo
     * @return
     */
    @Select("select * from scholarship_evaluating where stu_no = #{stuNo}")
    ScholarshipEvaluatingResult findScholarshipEvaluatingResultByStuNo(@Param("stuNo") String stuNo);

    /**
     * 查询所有评选过程数据
     *
     * @return
     */
    @Select("select * from scholarship_evaluating")
    List<ScholarshipEvaluatingResult> findScholarshipEvaluatingResults();

    /**
     * 查询归类的评选过程数据
     *
     * @return
     */
    @Select("select * from scholarship_evaluating where evaluation_result1=#{evaluationResult} or evaluation_result2=#{evaluationResult}")
    List<ScholarshipEvaluatingResult> findScholarshipConcludeEvaluatingResults(@Param("evaluationResult") String evaluationResult);
}
