package com.performance.analysis.dao;

import com.performance.analysis.pojo.EnglishEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author Tangwei
 * @date 2018/5/31 下午1:32
 * 英语考核数据访问
 */
@Mapper
@Repository
public interface EnglishEvaluationDao {

    /**
     * 添加英语成绩
     *
     * @param englishEvaluation 英语成绩
     */
    @Insert("insert or replace into english_evaluation(stu_no,english_score) " +
            "values(#{stuNo},#{englishScore})")
    void addEnglishEvaluation(EnglishEvaluation englishEvaluation);

    /**
     * 添加英语四级记录
     */
    @Insert("insert or ignore into english_cet4(stu_no) values(#{stuNo})")
    void addEnglishCET4(@Param("stuNo") String stuNo);

    /**
     * 查询所有的英语成绩
     *
     * @return List<EnglishEvaluation>
     */
    @Select("select english_evaluation.stu_no,english_evaluation.english_score,english_cet4.desc as cet4 from english_evaluation left join english_cet4 on english_evaluation.stu_no=english_cet4.stu_no")
    List<EnglishEvaluation> listEnglishEvaluation();
}
