package com.performance.analysis.service.impl;

import com.performance.analysis.commom.SystemCode;
import com.performance.analysis.exception.StorageException;
import com.performance.analysis.service.FileSystemStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午3:52
 * <p>
 * BUA Excel存储
 */

@Service
public class BuaExcelStorageService implements FileSystemStorageService {

    @Override
    public String store(MultipartFile file, String path) throws StorageException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            final Path rootLocation = Paths.get(path);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
            if (file.isEmpty()) {
                throw new StorageException(SystemCode.STORAGE_EMPTY_FILE.getMsg() + " " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException(SystemCode.STORAGE_INVALID_FILEPATH.getMsg() + " " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return rootLocation.resolve(filename).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new StorageException(SystemCode.STORAGE_ERROR.getMsg() + " " + filename, e);
        }
    }
}
