package com.performance.analysis;

import com.performance.analysis.common.SystemCode;
import com.performance.analysis.common.SystemResponse;
import com.performance.analysis.exception.DataAnalysisException;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.exception.StorageException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午8:55
 * <p>
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 自定义存储异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(StorageException.class)
    @ResponseBody
    public SystemResponse handleStorageException(StorageException e) {
        e.printStackTrace();
        return new SystemResponse(SystemCode.ERROR.getCode(), e.getMessage());
    }

    /**
     * 自定义数据读入异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DataReadInException.class)
    @ResponseBody
    public SystemResponse handleDataReadInException(DataReadInException e) {
        e.printStackTrace();
        return new SystemResponse(SystemCode.ERROR.getCode(), e.getMessage());
    }

    /**
     * 自定义数据分析异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DataAnalysisException.class)
    @ResponseBody
    public SystemResponse handleDataAnalysisException(DataAnalysisException e) {
        e.printStackTrace();
        return new SystemResponse(SystemCode.ERROR.getCode(), "数据不完整");
    }

    /**
     * 全局运行异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public SystemResponse handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return new SystemResponse(SystemCode.ERROR.getCode(), SystemCode.ERROR.getMsg());
    }
}
