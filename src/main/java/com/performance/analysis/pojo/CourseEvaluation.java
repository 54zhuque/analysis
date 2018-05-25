package com.performance.analysis.pojo;

/**
 * @Author: Tangwei
 * @Date: 2018/5/25 下午1:50
 * <p>
 * 专业课
 */
public class CourseEvaluation {
    private String name;//课程名称
    private Double credit;//课程应有学分
    private Double score;//课程得分

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
