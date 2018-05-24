package com.performance.analysis.service;

import com.performance.analysis.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午3:48
 * <p>
 * 文件保存
 */
public interface FileSystemStorageService {

    /**
     * 文件存储
     *
     * @param file 文件
     * @param path 存储目的
     * @return 存储文件路径
     * @throws StorageException
     */
    String store(MultipartFile file, String path) throws StorageException;
}
