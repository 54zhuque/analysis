package com.performance.analysis.pojo;

/**
 * 学生成绩
 *
 * @author tangwei
 * @since 1.0
 */
public class StudentScore {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 姓名
     */
    private String name;
    /**
     * 年级
     */
    private Integer grade;
    /**
     * 专业
     */
    private String major;

    /**
     * 身体素质分
     */
    Double physicalFixedScore;
    /**
     * 思想素质分
     */
    Double moralFixedScore;

    /**
     * 专业素质分
     */
    Double majorFixedScore;

    /**
     * 英语成绩
     */
    Double englishScore;

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getPhysicalFixedScore() {
        return physicalFixedScore;
    }

    public void setPhysicalFixedScore(Double physicalFixedScore) {
        this.physicalFixedScore = physicalFixedScore;
    }

    public Double getMoralFixedScore() {
        return moralFixedScore;
    }

    public void setMoralFixedScore(Double moralFixedScore) {
        this.moralFixedScore = moralFixedScore;
    }

    public Double getMajorFixedScore() {
        return majorFixedScore;
    }

    public void setMajorFixedScore(Double majorFixedScore) {
        this.majorFixedScore = majorFixedScore;
    }

    public Double getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(Double englishScore) {
        this.englishScore = englishScore;
    }
}
