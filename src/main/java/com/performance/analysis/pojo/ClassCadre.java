package com.performance.analysis.pojo;

import java.io.Serializable;

/**
 * 班干部
 *
 * @author tangwei
 * @since 1.0
 */
public class ClassCadre implements Serializable {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 学生姓名
     */
    private String stuName;
    /**
     * 描述（职务描述）
     */
    private String desc;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
