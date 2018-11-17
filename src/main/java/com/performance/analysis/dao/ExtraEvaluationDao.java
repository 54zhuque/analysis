package com.performance.analysis.dao;

import com.performance.analysis.pojo.ExtraEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Author: 段豆豆
 * @Date: 2018/11/17
 * 额外加分数据访问
 */
@Mapper
@Repository
public interface ExtraEvaluationDao {

    /**
     * 添加额外加分成绩
     * @param extraEvaluation
     */
    @Insert("insert or replace into extra_evaluation(stu_no,extra_score) " +
            "values(#{stuNo},#{extraScore})")
    void addExtraEvaluation(ExtraEvaluation extraEvaluation);

    /**
     * 查询所有的英语成绩
     *
     * @return List<EnglishEvaluation>
     */
    @Select("select * from extra_evaluation")
    List<ExtraEvaluation> listExraEvaluation();
}
