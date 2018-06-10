package com.performance.analysis.common;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午5:57
 * <p>
 * excel类型枚举
 */
public enum BuaExcelType {
    PHYSICAL("身体"),
    MORAL("思想"),
    MAJOY("专业"),
    ENGLISH("英语");

    private String name;

    BuaExcelType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
