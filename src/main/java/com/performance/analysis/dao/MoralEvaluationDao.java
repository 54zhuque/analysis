package com.performance.analysis.dao;

import com.performance.analysis.pojo.MoralEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
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

    @Insert("insert into moral_evaluation(stu_no,mate_score,teacher_score,dorm_score) " +
            "values(#{stuNo},#{mateScore},#{teacherScore},#{dormScore},#{fixScore})")
    void addMoralEvaluation(MoralEvaluation moralEvaluation);
}
