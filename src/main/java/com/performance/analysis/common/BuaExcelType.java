package com.performance.analysis.common;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午5:57
 * <p>
 * excel类型枚举
 */
public enum BuaExcelType {
    PHYSICAL("physical"),
    MORAL1("moral1"),
    MORAL2("moral2"),
    MAJOR1("major1"),
    MAJOR2("major2"),
    ENGLISH("english"),
    EXTRA("extra"),
    CADRE("cadre");

    private String name;

    BuaExcelType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
