package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: Tangwei
 * @Date: 2018/5/30 上午11:01
 * <p>
 * 英语考核
 */
public class EnglishEvaluation implements Serializable {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 姓名
     */
    private String stuName;
    /**
     * 英语成绩
     */
    private String englishScore;

    /**
     * 英语四级
     */
    private String cet4;

    private Student student;

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

    public String getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(String englishScore) {
        this.englishScore = englishScore;
    }

    public String getCet4() {
        return cet4;
    }

    public void setCet4(String cet4) {
        this.cet4 = cet4;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
