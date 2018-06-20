package com.performance.analysis.common;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午5:57
 * <p>
 * excel类型枚举
 */
public enum BuaExcelType {
    PHYSICAL("physical"),
    MORAL("moral"),
    MAJOY("majoy"),
    ENGLISH("english");

    private String name;

    BuaExcelType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
