package com.performance.analysis.service;

import com.performance.analysis.exception.DataReadInException;

import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:15
 */
public interface DataReadInService {
    /**
     * 数据读入
     *
     * @param args
     */
    void readIn(String... args) throws IOException, DataReadInException;
}
