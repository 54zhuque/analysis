package com.performance.analysis.service;

import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午1:54
 * <p>
 * 数据处理分析
 */
public interface DataAnalysisService {
    /**
     * 数据分析
     *
     * @param args
     * @return
     */
    List<T> analysis(String... args);
}
