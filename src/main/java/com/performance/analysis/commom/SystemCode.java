package com.performance.analysis.commom;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 上午10:22
 * <p>
 * 返回状态码枚举
 */
public enum SystemCode {
    SUCCESS(200, "操作成功."),
    ERROR(500, "系统错误."),
    FILE_NOT_FIND(10000, "找不到文件."),
    READIN_MUST_EXCEL(10001, "读取文件必须为Excel文件"),
    READIN_ERROR(10002, "数据读入错误"),
    STORAGE_EMPTY_FILE(10003, "无法存储空文件"),
    STORAGE_INVALID_FILEPATH(10004, "无法存储非法路径"),
    STORAGE_ERROR(10005, "存储错误");

    private int code;
    private String msg;

    private SystemCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "[" + this.code + "]" + this.msg;
    }
}
