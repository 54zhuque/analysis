package com.performance.analysis.dao;

import com.performance.analysis.pojo.MoralEvaluation;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午3:04
 * <p>
 * 思想素质数据访问
 */
@Mapper
@Repository
public interface MoralEvaluationDao {

    /**
     * 添加思想素质考核评分
     *
     * @param moralEvaluation
     */
    @Insert("insert or replace into moral_evaluation(stu_no,mate_score,teacher_score,dorm_score,fix_score) " +
            "values(#{stuNo},#{mateScore},#{teacherScore},#{dormScore},#{fixScore})")
    void addMoralEvaluation(MoralEvaluation moralEvaluation);

    /**
     * 查询所有的专业学科成绩
     *
     * @return List<MoralEvaluation>
     */
    @Select("select * from moral_evaluation")
    List<MoralEvaluation> listMoralEvaluation();
}
