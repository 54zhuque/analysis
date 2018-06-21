package com.performance.analysis.dao;

import com.performance.analysis.pojo.EnglishEvaluation;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


/**
 * @author Tangwei
 * @date 2018/5/31 下午1:32
 *  英语考核数据访问
 */
@Mapper
@Repository
public interface EnglishEvaluationDao {

    /**
     * 添加英语成绩
     *
     * @param englishEvaluation 英语成绩
     */
    @Insert("insert or ignore into english_evaluation(stu_no,english_score) " +
            "values(#{stuNo},#{englishScore})")
    void addEnglishEvaluation(EnglishEvaluation englishEvaluation);

    /**
     * 查询所有的英语成绩
     * @return List<EnglishEvaluation>
     */
    @Select("select * from english_evaluation")
    List<EnglishEvaluation> listEnglishEvaluation();
}
