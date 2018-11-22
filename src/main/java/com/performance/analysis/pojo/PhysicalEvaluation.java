package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:32
 * <p>
 * 身体素质成绩考核
 */
public class PhysicalEvaluation implements Serializable {
    private String stuNo;//学号
    private String name;//姓名
    private Double cultureScore;//体育成绩
    private Double trainingScore;//体侧成绩
    private Double additionalPlus;//额外加分
    private Double fixScore;//加权
    private Student student;

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

    public Double getCultureScore() {
        return cultureScore;
    }

    public void setCultureScore(Double cultureScore) {
        this.cultureScore = cultureScore;
    }

    public Double getTrainingScore() {
        return trainingScore;
    }

    public void setTrainingScore(Double trainingScore) {
        this.trainingScore = trainingScore;
    }

    public Double getAdditionalPlus() {
        return additionalPlus;
    }

    public void setAdditionalPlus(Double additionalPlus) {
        this.additionalPlus = additionalPlus;
    }

    public Double getFixScore() {
        return fixScore;
    }

    public void setFixScore(Double fixScore) {
        this.fixScore = fixScore;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
