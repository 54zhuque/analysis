package com.performance.analysis.common;

/**
 * BUA评优枚举
 *
 * @author tangwei
 * @since 1.0
 */
public enum BuaEvaluationEnum {
    STUDENT_MODEL("A", "三好学生"),
    CLASS_CADRE_MODEL("B", "优秀学生干部");

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
