package com.performance.analysis.commom;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午4:34
 * <p>
 * 返回状态码
 */
public class SystemResponse<T> {
    private int code;
    private String msg;
    private T data;

    public SystemResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SystemResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
