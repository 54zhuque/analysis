package com.performance.analysis.common;

/**
 * BUA评优枚举
 *
 * @author tangwei
 * @since 1.0
 */
public enum BuaEvaluationEnum {
    TRIPLEA("A", "三好学生");

    private String value;
    private String desc;

    BuaEvaluationEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
