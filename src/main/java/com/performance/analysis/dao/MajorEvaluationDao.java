package com.performance.analysis.dao;

import com.performance.analysis.pojo.MajorEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/25 下午4:23
 * <p>
 * 专业素质数据访问
 */
@Mapper
@Repository
public interface MajorEvaluationDao {

    /**
     * 添加专业成绩考核评分
     *
     * @param stuNo
     * @param course
     * @param fixScore
     */
    @Insert("insert or replace into major_evaluation(stu_no,course,fix_score) " +
            "values(#{stuNo},#{course},#{fixScore})")
    void addMajorEvaluation(@Param("stuNo") String stuNo, @Param("course") String course, @Param("fixScore") Double fixScore);

    /**
     * 查询所有的专业学科成绩
     *
     * @return List<MajorEvaluation>
     */
    @Select("select * from major_evaluation")
    List<MajorEvaluation> listMajorEvaluation();
}
