package com.performance.analysis.dto;

import com.alibaba.fastjson.JSON;
import com.performance.analysis.pojo.CourseEvaluation;
import com.performance.analysis.service.impl.BuaAnalyticalRule;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学生成绩
 *
 * @author tangwei
 * @since 1.0
 */
public class StudentScoreDto implements Serializable {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 姓名
     */
    private String stuName;
    /**
     * 年级
     */
    private Integer stuGrade;
    /**
     * 专业
     */
    private String stuMajor;

    /**
     * 学生成绩
     */
    private String stuCourse;
    /**
     * 身体素质计算分
     */
    private Double physicalScore = 0.0d;
    /**
     * 思想素质计算分
     */
    private Double moralScore = 0.0d;
    /**
     * 专业成绩计算分
     */
    private Double majorScore = 0.0d;
    /**
     * 英语成绩
     */
    private Double englishScore = 0.0d;
    /**
     * 额外加分（发展性素质分）
     */
    private Double extraScore = 0.0d;

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public Integer getStuGrade() {
        return stuGrade;
    }

    public void setStuGrade(Integer stuGrade) {
        this.stuGrade = stuGrade;
    }

    public String getStuMajor() {
        return stuMajor;
    }

    public void setStuMajor(String stuMajor) {
        this.stuMajor = stuMajor;
    }

    public Double getPhysicalScore() {
        return physicalScore;
    }

    public void setPhysicalScore(Double physicalScore) {
        this.physicalScore = physicalScore;
    }

    public Double getMoralScore() {
        return moralScore;
    }

    public void setMoralScore(Double moralScore) {
        this.moralScore = moralScore;
    }

    public Double getMajorScore() {
        return majorScore;
    }

    public void setMajorScore(Double majorScore) {
        this.majorScore = majorScore;
    }

    public Double getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(Double englishScore) {
        this.englishScore = englishScore;
    }


    public Double getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(Double extraScore) {
        this.extraScore = extraScore;
    }

    public List<CourseEvaluation> getStuCourse() {
        if (StringUtils.isEmpty(stuCourse)) {
            return null;
        }
        return JSON.parseArray(stuCourse, CourseEvaluation.class);
    }

    public void setStuCourse(String stuCourse) {
        this.stuCourse = stuCourse;
    }

    /**
     * 基础素质分=体育*权重+道德*权重+学科*权重
     *
     * @return 基础素质分
     */
    public Double getBasicScore() {
        //基础素质评优权重
        Double[] weights = new Double[]{0.2d, 0.6d, 0.2d};
        return BuaAnalyticalRule.getWeightedScore(weights, physicalScore, majorScore, moralScore);
    }

    /**
     * 综合素质分=基础素质分+额外加分
     *
     * @return 综合素质分
     */
    public Double getFixScore() {
        BigDecimal basicScoreDecimal = new BigDecimal(this.getBasicScore());
        BigDecimal extraScoreDecimal = new BigDecimal(this.extraScore);
        return basicScoreDecimal.add(extraScoreDecimal).doubleValue();
    }

}
