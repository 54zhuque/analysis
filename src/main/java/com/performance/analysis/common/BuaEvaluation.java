package com.performance.analysis.common;

/**
 * 评选内容
 *
 * @author tangwei
 * @since 1.0
 */
public class BuaEvaluation {
    /**
     * 年级
     */
    private Integer grade;
    /**
     * 专业
     */
    private String major;
    /**
     * 评选结果
     */
    private String evaluationResult;

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

    public String getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(String evaluationResult) {
        this.evaluationResult = evaluationResult;
    }
}
