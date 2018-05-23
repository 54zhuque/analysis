package com.performance.analysis.service;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:15
 */
public interface DataReadIn {
    /**
     * 数据读入
     *
     * @param args
     */
    void readIn(String... args) throws IOException;
}
