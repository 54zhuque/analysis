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
    private Double cultureScore;//体育成绩
    private Double trainingScore;//体侧成绩
    private Double additionalPlus;//
    private Double fixScore;

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
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
}
