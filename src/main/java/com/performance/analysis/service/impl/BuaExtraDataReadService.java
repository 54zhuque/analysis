package com.performance.analysis.service.impl;

import com.performance.analysis.dao.ExtraEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.ExtraEvaluation;
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
 * @Author: 段豆豆
 * @Date: 2018/11/17
 * <p>
 * BUA 英语成绩读入
 */
@Service
public class BuaExtraDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private ExtraEvaluationDao extraEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<ExtraEvaluation> extraEvaluations = this.readInExtraEvaluation(workbook);
        for (ExtraEvaluation extraEvaluation : extraEvaluations) {
            Student stu = new Student();
            stu.setStuNo(extraEvaluation.getStuNo());
            stu.setName(extraEvaluation.getStuName());
            stu.setGrade(BuaAnalyticalRule.getGrade(extraEvaluation.getStuNo()));
            stu.setMajor(BuaAnalyticalRule.getMajor(extraEvaluation.getStuNo()));
            studentDao.addStudent(stu);
            extraEvaluationDao.addExtraEvaluation(extraEvaluation);
        }
    }

    @Override
    public void readMerge(File file) throws IOException, DataReadInException {

    }

    /**
     * 读入英语成绩
     *
     * @param workbook
     * @return
     */
    private List<ExtraEvaluation> readInExtraEvaluation(Workbook workbook) {
        List<ExtraEvaluation> extraEvaluations = new ArrayList<>(30);
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
            ExtraEvaluation extraEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                if (StringUtils.isEmpty(stuNo)) {
                    continue;
                }
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                String extraScore = ExcelUtil.getCellValue(row.getCell(2));
                extraEvaluation = new ExtraEvaluation();
                extraEvaluation.setExtraScore(extraScore);
                extraEvaluation.setStuName(stuName);
                extraEvaluation.setStuNo(stuNo);
                extraEvaluations.add(extraEvaluation);
            }
        }
        return extraEvaluations;
    }
}
