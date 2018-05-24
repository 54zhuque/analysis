package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:36
 * <p>
 * 思想素质成绩考核
 */
public class MoralEvaluation implements Serializable {
    private String stuNo;//学号
    private String name;//姓名
    private Double mateScore;//互评分
    private Double teacherScore;//教师评分
    private Double dormScore;//宿舍表现分
    private Double fixScore;//加权平均分

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

    public Double getMateScore() {
        return mateScore;
    }

    public void setMateScore(Double mateScore) {
        this.mateScore = mateScore;
    }

    public Double getTeacherScore() {
        return teacherScore;
    }

    public void setTeacherScore(Double teacherScore) {
        this.teacherScore = teacherScore;
    }

    public Double getDormScore() {
        return dormScore;
    }

    public void setDormScore(Double dormScore) {
        this.dormScore = dormScore;
    }

    public Double getFixScore() {
        return fixScore;
    }

    public void setFixScore(Double fixScore) {
        this.fixScore = fixScore;
    }
}
