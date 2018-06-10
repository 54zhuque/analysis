package com.performance.analysis.dao;

import com.performance.analysis.pojo.EnglishEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午1:32
 * <p>
 * 英语考核数据访问
 */
@Mapper
@Repository
public interface EnglishEvaluationDao {

    /**
     * 添加英语成绩
     *
     * @param englishEvaluation
     */
    @Insert("insert or ignore into english_evaluation(stu_no,english_score) " +
            "values(#{stuNo},#{englishScore})")
    void addEnglishEvaluation(EnglishEvaluation englishEvaluation);
}
