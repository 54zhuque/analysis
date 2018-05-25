package com.performance.analysis.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: Tangwei
 * @Date: 2018/5/25 下午4:23
 * <p>
 * 专业素质数据访问
 */
@Mapper
@Repository
public interface MajorEvaluationDao {

    @Insert("insert or ignore into major_evaluation(stu_no,course,fix_score) " +
            "values(#{stuNo},#{course},#{fixScore})")
    void addMajorEvaluation(String stuNo, String course, Double fixScore);
}
