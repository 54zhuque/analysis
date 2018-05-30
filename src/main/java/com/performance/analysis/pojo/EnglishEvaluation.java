package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * @Author: Tangwei
 * @Date: 2018/5/30 上午11:01
 * <p>
 * 英语考核
 */
public class EnglishEvaluation implements Serializable {
    private String stuNo;//学号
    private String englishScore;//英语成绩，大一为分数，其他为CET-4或空

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(String englishScore) {
        this.englishScore = englishScore;
    }
}
