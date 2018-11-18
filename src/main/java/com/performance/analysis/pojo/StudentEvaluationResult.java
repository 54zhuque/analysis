package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午2:17
 * <p>
 * 学生考核
 */
public class StudentEvaluationResult implements Serializable {
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
     * 综合评分
     */
    private Double fixScore;
    /**
     * 额外加分（发展性素质分）
     */
    private Double extraScore;
    /**
     * 考核结果
     */
    private String evaluationResult;

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

    public Double getFixScore() {
        return fixScore;
    }

    public void setFixScore(Double fixScore) {
        this.fixScore = fixScore;
    }

    public String getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(String englishScore) {
        this.englishScore = englishScore;
    }

    public Double getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(Double extraScore) {
        this.extraScore = extraScore;
    }

    public String getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(String evaluationResult) {
        this.evaluationResult = evaluationResult;
    }
}
