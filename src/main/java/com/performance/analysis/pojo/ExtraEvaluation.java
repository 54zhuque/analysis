package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: 段豆豆
 * @Date: 2018/11/17
 * 额外加分
 */
public class ExtraEvaluation implements Serializable {
    private String stuNo;
    private String stuName;
    private String extraScore;

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

    public String getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(String extraScore) {
        this.extraScore = extraScore;
    }
}
