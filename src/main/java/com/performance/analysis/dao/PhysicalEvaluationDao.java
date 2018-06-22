package com.performance.analysis.dao;

import com.performance.analysis.pojo.PhysicalEvaluation;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
    @Insert("insert or replace into physical_evaluation(stu_no,culture_score,training_score,additional_plus,fix_score) " +
            "values(#{stuNo},#{cultureScore},#{trainingScore},#{additionalPlus},#{fixScore})")
    void addPhysicalEvaluation(PhysicalEvaluation physicalEvaluation);

    /**
     * 查询所有的体育成绩
     *
     * @return List<PhysicalEvaluation>
     */
    @Select("select * from physical_evaluation")
    List<PhysicalEvaluation> listPhysicalEvaluation();

}
