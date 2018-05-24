package com.performance.analysis.controller;

import com.performance.analysis.common.SystemCode;
import com.performance.analysis.common.SystemResponse;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.exception.StorageException;
import com.performance.analysis.service.DataReadInService;
import com.performance.analysis.service.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午4:47
 * <p>
 * BUA 评优分析
 */
@RestController
public class BuaAnalysisController {

    @Autowired
    private FileSystemStorageService buaExcelStorageService;
    @Autowired
    private DataReadInService buaExcelDataReadInService;

    @PostMapping("/bua/analysis/upload")
    @ResponseBody
    public SystemResponse handleBuaExcel(@RequestParam("file") MultipartFile file) throws StorageException, IOException, DataReadInException {
        String path = buaExcelStorageService.store(file, "bua-excels");
        buaExcelDataReadInService.readIn(path);
        return new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
    }


}
