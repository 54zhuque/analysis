package com.performance.analysis.exception;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午4:03
 * <p>
 * 存储异常
 */
public class StorageException extends Exception {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }


}
