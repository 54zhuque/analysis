package com.performance.analysis.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/25 下午1:50
 * <p>
 * 专业课程成绩考核
 */
public class MajorEvaluation implements Serializable {
    private String stuNo;//学号
    private String stuName;//姓名
    private List<CourseEvaluation> courseEvaluations;//课程
    private Double fixScore;//加权平均分（学分加权）
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

    public List<CourseEvaluation> getCourseEvaluations() {
        return courseEvaluations;
    }

    public void setCourseEvaluations(List<CourseEvaluation> courseEvaluations) {
        this.courseEvaluations = courseEvaluations;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
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
