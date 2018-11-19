package com.performance.analysis.service.impl;

import com.performance.analysis.dao.PhysicalEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.PhysicalEvaluation;
import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.FileDataReadService;
import com.performance.analysis.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/29 下午3:46
 * <p>
 * BUA身体素质数据读入
 */
@Service
public class BuaPhysicalDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private PhysicalEvaluationDao physicalEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<PhysicalEvaluation> physicalEvaluations = this.readInPhysicalEvaluation(workbook);
        for (PhysicalEvaluation physicalEvaluation : physicalEvaluations) {
            Integer grade = BuaAnalyticalRule.getGrade(physicalEvaluation.getStuNo());
            Student stu = new Student();
            stu.setName(physicalEvaluation.getName());
            stu.setGrade(grade);
            stu.setStuNo(physicalEvaluation.getStuNo());
            stu.setMajor(BuaAnalyticalRule.getMajor(physicalEvaluation.getStuNo()));
            studentDao.addStudent(stu);
            Double[] weights = BuaAnalyticalRule.getPhysicalWeights(grade);
            physicalEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, physicalEvaluation.getCultureScore(),
                    physicalEvaluation.getTrainingScore(), physicalEvaluation.getAdditionalPlus()));
            physicalEvaluationDao.addPhysicalEvaluation(physicalEvaluation);
        }
    }

    @Override
    public void readMerge(File file) throws IOException, DataReadInException {

    }

    /**
     * 读入身体素质评分
     *
     * @param workbook
     * @return
     */
    private List<PhysicalEvaluation> readInPhysicalEvaluation(Workbook workbook) {
        List<PhysicalEvaluation> physicalEvaluations = new ArrayList<>(30);
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                continue;
            }
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            PhysicalEvaluation physicalEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                physicalEvaluation = new PhysicalEvaluation();
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                String cultureScoreCellValue = ExcelUtil.getCellValue(row.getCell(2));
                Double cultureScore = StringUtils.isEmpty(cultureScoreCellValue) ?
                        0 : Double.valueOf(cultureScoreCellValue);
                String trainingScoreCellValue = ExcelUtil.getCellValue(row.getCell(3));
                Double trainingScore = StringUtils.isEmpty(trainingScoreCellValue) ?
                        0 : Double.valueOf(trainingScoreCellValue);
                String additionalPlusCellValue = ExcelUtil.getCellValue(row.getCell(4));
                Double additionalPlus = StringUtils.isEmpty(additionalPlusCellValue) ?
                        0 : Double.valueOf(additionalPlusCellValue);
                physicalEvaluation.setAdditionalPlus(additionalPlus);
                physicalEvaluation.setCultureScore(cultureScore);
                physicalEvaluation.setStuNo(stuNo);
                physicalEvaluation.setName(stuName);
                physicalEvaluation.setTrainingScore(trainingScore);
                physicalEvaluations.add(physicalEvaluation);
            }
        }
        return physicalEvaluations;
    }
}
