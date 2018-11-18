package com.performance.analysis.common;

/**
 * BUA评优枚举
 *
 * @author tangwei
 * @since 1.0
 */
public enum BuaEvaluationEnum {
    STUDENT_MODEL("A", "三好学生"),
    CLASS_CADRE_MODEL("B", "优秀学生干部"),
    SCHOLARSHIP("W", "奖学金"),
    SCHOLARSHIP_W0("W0", "特等奖学金"),
    SCHOLARSHIP_W1("W1", "一等奖学金"),
    SCHOLARSHIP_W2("W2", "二等奖学金"),
    SCHOLARSHIP_W3("W3", "三等奖学金");

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
