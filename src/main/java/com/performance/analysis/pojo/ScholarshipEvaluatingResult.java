package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * 奖学金评选过程
 *
 * @author tangwei
 * @since 1.0
 */
public class ScholarshipEvaluatingResult implements Serializable {
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
     * 身体素质计算分
     */
    private Double physicalScore;
    /**
     * 思想素质计算分
     */
    private Double moralScore;
    /**
     * 专业成绩计算分
     */
    private Double majorScore;
    /**
     * 英语成绩
     */
    private String englishScore;
    /**
     * 基础素质分=体育*权重+道德*权重+学科*权重
     */
    private Double basicScore;
    /**
     * 额外加分（发展性素质分）
     */
    private Double extraScore;

    /**
     * 综合评分
     */
    private Double fixScore;
    /**
     * 候选考核结果
     */
    private String evaluationResult1;
    /**
     * 手选确认结果
     */
    private String evaluationResult2;

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

    public String getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(String englishScore) {
        this.englishScore = englishScore;
    }

    public Double getBasicScore() {
        return basicScore;
    }

    public void setBasicScore(Double basicScore) {
        this.basicScore = basicScore;
    }

    public Double getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(Double extraScore) {
        this.extraScore = extraScore;
    }

    public Double getFixScore() {
        return fixScore;
    }

    public void setFixScore(Double fixScore) {
        this.fixScore = fixScore;
    }

    public String getEvaluationResult1() {
        return evaluationResult1;
    }

    public void setEvaluationResult1(String evaluationResult1) {
        this.evaluationResult1 = evaluationResult1;
    }

    public String getEvaluationResult2() {
        return evaluationResult2;
    }

    public void setEvaluationResult2(String evaluationResult2) {
        this.evaluationResult2 = evaluationResult2;
    }
}
