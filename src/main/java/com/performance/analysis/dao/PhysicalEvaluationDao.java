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

    @Insert("insert into physical_evaluation(stu_no,culture_score,training_score,additional_plus) " +
            "values(#{stuNo},#{cultureScore},#{trainingScore},#{additionalPlus},#{fixScore})")
    void addPhysicalEvaluation(PhysicalEvaluation physicalEvaluation);

}
