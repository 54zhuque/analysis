package com.performance.analysis.service.impl;

import com.performance.analysis.dao.EnglishEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.EnglishEvaluation;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/30 上午11:00
 * <p>
 * BUA 英语成绩读入
 */
@Service
public class BuaEnglishDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private EnglishEvaluationDao englishEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<EnglishEvaluation> englishEvaluations = this.readInEnglishEvaluation(workbook);
        for (EnglishEvaluation englishEvaluation : englishEvaluations) {
            Student stu = new Student();
            stu.setStuNo(englishEvaluation.getStuNo());
            stu.setName(englishEvaluation.getStuName());
            stu.setGrade(BuaAnalyticalRule.getGrade(englishEvaluation.getStuNo()));
            stu.setMajor(BuaAnalyticalRule.getMajor(englishEvaluation.getStuNo()));
            studentDao.addStudent(stu);
            englishEvaluationDao.addEnglishEvaluation(englishEvaluation);
        }
    }

    /**
     * 读入英语成绩
     *
     * @param workbook
     * @return
     */
    private List<EnglishEvaluation> readInEnglishEvaluation(Workbook workbook) {
        List<EnglishEvaluation> englishEvaluations = new ArrayList<>(30);
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                continue;
            }
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (firstRow == null) {
                continue;
            }
            EnglishEvaluation englishEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                englishEvaluation = new EnglishEvaluation();
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                String englishScore = ExcelUtil.getCellValue(row.getCell(2));
                englishEvaluation.setEnglishScore(englishScore);
                englishEvaluation.setStuName(stuName);
                englishEvaluation.setStuNo(stuNo);
                englishEvaluations.add(englishEvaluation);
            }
        }
        return englishEvaluations;
    }
}
