package com.performance.analysis.exception;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 上午10:15
 * <p>
 * 数据分析异常
 */
public class DataAnalysisException extends Exception {
    public DataAnalysisException(String message) {
        super(message);
    }

    public DataAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
