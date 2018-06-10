package com.performance.analysis.controller;

import com.performance.analysis.common.BuaExcelType;
import com.performance.analysis.common.SystemCode;
import com.performance.analysis.common.SystemResponse;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.exception.StorageException;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaDataAnalysisService;
import com.performance.analysis.service.FileDataReadService;
import com.performance.analysis.service.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private FileDataReadService buaPhysicalDataReadService;
    @Autowired
    private FileDataReadService buaMoralDataReadService;
    @Autowired
    private FileDataReadService buaMajorDataReadService;
    @Autowired
    private FileDataReadService buaEnglishDataReadService;
    @Autowired
    private BuaDataAnalysisService buaTripleAResultService;

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
        } else if (fileName.contains(BuaExcelType.ENGLISH.toString())) {
            buaEnglishDataReadService.read(uploadFile);
        } else {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
        return new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
    }

    /**
     * 评优分析
     *
     * @param majorGrade 专业年级，学号前几位中获取
     * @param type       A A++ A+ etc...
     * @return
     * @throws DataReadInException
     */
    @PostMapping("/bua/analysis/evaluations/{majorGrade}/{type}")
    @ResponseBody
    public SystemResponse<List<StudentEvaluationResult>> handleBuaStudentEvaluation(@PathVariable String majorGrade, @PathVariable String type) throws DataReadInException {
        SystemResponse response = new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
        List<StudentEvaluationResult> studentEvaluationResults;
        switch (type) {
            case "A":
                studentEvaluationResults = buaTripleAResultService.majorGradeAnalysis(majorGrade);
                break;
            default:
                studentEvaluationResults = null;
        }
        response.setData(studentEvaluationResults);
        return response;
    }

}
