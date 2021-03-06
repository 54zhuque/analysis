package com.performance.analysis.controller;

import com.performance.analysis.common.*;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.exception.StorageException;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaEvaluationService;
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
    private FileDataReadService buaExtraDataReadService;
    @Autowired
    private FileDataReadService buaCadreDataReadService;
    @Autowired
    private BuaEvaluationService studentModelEvaluationService;
    @Autowired
    private BuaEvaluationService classCadreModelEvaluationService;
    @Autowired
    private BuaEvaluationService scholarshipEvaluationService;

    /**
     * Excel上传数据处理
     *
     * @param file
     * @param type
     * @return
     * @throws StorageException
     * @throws IOException
     * @throws DataReadInException
     */
    @PostMapping("/bua/analysis/upload/{type}")
    @ResponseBody
    public SystemResponse handleBuaExcel(@RequestParam("file") MultipartFile file, @PathVariable String type) throws StorageException, IOException, DataReadInException {
        String path = buaExcelStorageService.store(file, UPLOAD_PATH);
        File uploadFile = new File(path);
        if (type.toLowerCase().equals(BuaExcelType.PHYSICAL.toString())) {
            buaPhysicalDataReadService.read(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.MORAL1.toString())) {
            buaMoralDataReadService.read(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.MORAL2.toString())) {
            //下学期合并上学期数据
            buaMoralDataReadService.readMerge(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.MAJOR1.toString())) {
            buaMajorDataReadService.read(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.MAJOR2.toString())) {
            //下学期合并上学期数据
            buaMajorDataReadService.readMerge(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.ENGLISH.toString())) {
            buaEnglishDataReadService.read(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.EXTRA.toString())) {
            buaExtraDataReadService.read(uploadFile);
        } else if (type.toLowerCase().equals(BuaExcelType.CADRE.toString())) {
            buaCadreDataReadService.read(uploadFile);
        } else {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
        return new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
    }

    /**
     * 学生评优
     *
     * @param grade 年级
     * @param type  类型A、B、W
     * @return
     */
    @GetMapping("/bua/analysis/evaluations/{grade}/{type}")
    @ResponseBody
    public SystemResponse<List<StudentEvaluationResult>> handleBuaStudentEvaluation(@PathVariable Integer grade, @PathVariable String type) {
        SystemResponse response = new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
        List<StudentEvaluationResult> studentEvaluationResults;
        BuaEvaluation evaluation = new BuaEvaluation();
        evaluation.setEvaluationResult(type);
        evaluation.setGrade(grade);
        if (BuaEvaluationEnum.STUDENT_MODEL.getValue().equals(type)) {
            studentEvaluationResults = studentModelEvaluationService.evaluate(evaluation);
        } else if (BuaEvaluationEnum.CLASS_CADRE_MODEL.getValue().equals(type)) {
            studentEvaluationResults = classCadreModelEvaluationService.evaluate(evaluation);
        } else if (BuaEvaluationEnum.SCHOLARSHIP.getValue().equals(type)) {
            studentEvaluationResults = scholarshipEvaluationService.evaluate(evaluation);
        } else if (BuaEvaluationEnum.SCHOLARSHIP.getValue().equals(type)) {
            studentEvaluationResults = scholarshipEvaluationService.evaluate(evaluation);
        } else {
            studentEvaluationResults = null;
        }

        response.setData(studentEvaluationResults);
        return response;
    }

}
