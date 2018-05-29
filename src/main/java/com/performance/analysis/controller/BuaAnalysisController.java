package com.performance.analysis.controller;

import com.performance.analysis.common.BuaExcelType;
import com.performance.analysis.common.SystemCode;
import com.performance.analysis.common.SystemResponse;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.exception.StorageException;
import com.performance.analysis.service.FileSystemStorageService;
import com.performance.analysis.service.impl.BuaMajorDataReadService;
import com.performance.analysis.service.impl.BuaMoralDataReadService;
import com.performance.analysis.service.impl.BuaPhysicalDataReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午4:47
 * <p>
 * BUA 评优分析
 */
@RestController
public class BuaAnalysisController {
    private static final String UPLOAD_PATH = "bua-excels";
    @Autowired
    private FileSystemStorageService buaExcelStorageService;
    @Autowired
    private BuaPhysicalDataReadService buaPhysicalDataReadService;
    @Autowired
    private BuaMoralDataReadService buaMoralDataReadService;
    @Autowired
    private BuaMajorDataReadService buaMajorDataReadService;

    /**
     * Excel上传数据处理
     *
     * @param file
     * @return
     * @throws StorageException
     * @throws IOException
     * @throws DataReadInException
     */
    @PostMapping("/bua/analysis/upload")
    @ResponseBody
    public SystemResponse handleBuaExcel(@RequestParam("file") MultipartFile file) throws StorageException, IOException, DataReadInException {
        String path = buaExcelStorageService.store(file, UPLOAD_PATH);
        File uploadFile = new File(path);
        String fileName = uploadFile.getName();
        if (fileName.contains(BuaExcelType.PHYSICAL.toString())) {
            buaPhysicalDataReadService.read(uploadFile);
        } else if (fileName.contains(BuaExcelType.MORAL.toString())) {
            buaMoralDataReadService.read(uploadFile);
        } else if (fileName.contains(BuaExcelType.MAJOY.toString())) {
            buaMajorDataReadService.read(uploadFile);
        } else {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
        return new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
    }


}
