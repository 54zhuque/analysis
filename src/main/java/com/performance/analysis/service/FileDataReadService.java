package com.performance.analysis.service;

import com.performance.analysis.exception.DataReadInException;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/29 下午3:42
 * <p>
 * 文件数据读入
 */
public interface FileDataReadService {
    /**
     * 文件数据读入
     *
     * @param file 文件
     * @throws IOException
     * @throws DataReadInException
     */
    void read(File file) throws IOException, DataReadInException;

    /**
     * 文件数据合并读入
     *
     * @param file
     * @throws IOException
     * @throws DataReadInException
     */
    void readMerge(File file) throws IOException, DataReadInException;
}
