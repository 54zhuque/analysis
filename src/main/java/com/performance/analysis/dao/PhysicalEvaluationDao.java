package com.performance.analysis.dao;

import com.performance.analysis.pojo.PhysicalEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:57
 * <p>
 * 学生身体素质数据访问
 */
@Mapper
@Repository
public interface PhysicalEvaluationDao {

    /**
     * 添加身体素质考核评分
     *
     * @param physicalEvaluation
     */
    @Insert("insert or ignore into physical_evaluation(stu_no,culture_score,training_score,additional_plus,fix_score) " +
            "values(#{stuNo},#{cultureScore},#{trainingScore},#{additionalPlus},#{fixScore})")
    void addPhysicalEvaluation(PhysicalEvaluation physicalEvaluation);

}
