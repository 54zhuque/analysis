package com.performance.analysis.service.impl;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午5:57
 * <p>
 * excel类型枚举
 */
public enum BuaExcelEnum {
    PHYSICAL("身体"),
    MORAL("思想"),
    MAJOY("专业");

    private String name;

    private BuaExcelEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
