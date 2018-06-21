package com.performance.analysis.dto;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午2:35
 * <p>
 * 学生考核DTO
 */
public class StudentEvaluationDto {
    private String stuNo;//学号
    private String stuName;//姓名
    private Integer stuGrade;//年级
    private Double physicalScore = 0.0d;//身体素质计算分
    private Double moralScore = 0.0d;//思想素质计算分
    private Double majorScore = 0.0d;//专业成绩计算分
    private String englishScore = "0";//英语成绩

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
}
