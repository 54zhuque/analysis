package com.performance.analysis.service.impl;

import com.performance.analysis.dao.CadreEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.ClassCadre;
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
 * @Author: 段豆豆
 * @Date: 2018/11/17
 * <p>
 * BUA 英语成绩读入
 */
@Service
public class BuaCadreDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private CadreEvaluationDao cadreEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<ClassCadre> cadreEvaluations = this.readInExtraEvaluation(workbook);
        for (ClassCadre cadreEvaluation : cadreEvaluations) {
            Student stu = new Student();
            stu.setStuNo(cadreEvaluation.getStuNo());
            stu.setName(cadreEvaluation.getStuName());
            stu.setGrade(BuaAnalyticalRule.getGrade(cadreEvaluation.getStuNo()));
            stu.setMajor(BuaAnalyticalRule.getMajor(cadreEvaluation.getStuNo()));
            studentDao.addStudent(stu);
            cadreEvaluationDao.addCadreEvaluation(cadreEvaluation);
        }
    }

    /**
     * 读入英语成绩
     *
     * @param workbook
     * @return
     */
    private List<ClassCadre> readInExtraEvaluation(Workbook workbook) {
        List<ClassCadre> cadreEvaluations = new ArrayList<>(30);
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
            ClassCadre cadreEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                cadreEvaluation = new ClassCadre();
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                String desc = ExcelUtil.getCellValue(row.getCell(2));
                cadreEvaluation.setDesc(desc);
                cadreEvaluation.setStuName(stuName);
                cadreEvaluation.setStuNo(stuNo);
                cadreEvaluations.add(cadreEvaluation);
            }
        }
        return cadreEvaluations;
    }
}
