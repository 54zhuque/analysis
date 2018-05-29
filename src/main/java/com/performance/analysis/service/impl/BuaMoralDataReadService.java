package com.performance.analysis.service.impl;

import com.performance.analysis.dao.MoralEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.MoralEvaluation;
import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.FileDataReadService;
import com.performance.analysis.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/29 下午3:59
 * <p>
 * BUA思想素质数据读入
 */
@Service
public class BuaMoralDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private MoralEvaluationDao moralEvaluationDao;

    @Override
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<MoralEvaluation> moralEvaluations = this.readInMoralEvaluation(workbook);
        for (MoralEvaluation moralEvaluation : moralEvaluations) {
            Integer grade = BuaAnalyticalRule.getGrade(moralEvaluation.getStuNo());
            Student stu = new Student(moralEvaluation.getStuNo(), moralEvaluation.getStuName(), grade);
            studentDao.addStudent(stu);
            Double[] weights = BuaAnalyticalRule.getMoralWeights();
            moralEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, moralEvaluation.getMateScore(),
                    moralEvaluation.getTeacherScore(), moralEvaluation.getDormScore()));
            moralEvaluationDao.addMoralEvaluation(moralEvaluation);
        }
    }

    /**
     * 读入思想素质评分
     *
     * @param workbook
     * @return
     */
    private List<MoralEvaluation> readInMoralEvaluation(Workbook workbook) {
        List<MoralEvaluation> moralEvaluations = new ArrayList<>(30);
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                continue;
            }
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            MoralEvaluation moralEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                moralEvaluation = new MoralEvaluation();
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                String mateScoreCellValue = ExcelUtil.getCellValue(row.getCell(2));
                Double mateScore = StringUtils.isEmpty(mateScoreCellValue) ?
                        0 : Double.valueOf(mateScoreCellValue);
                String teacherScoreCellValue = ExcelUtil.getCellValue(row.getCell(3));
                Double teacherScore = StringUtils.isEmpty(teacherScoreCellValue) ?
                        0 : Double.valueOf(teacherScoreCellValue);
                String dormScoreCellValue = ExcelUtil.getCellValue(row.getCell(4));
                Double dormScore = StringUtils.isEmpty(dormScoreCellValue) ?
                        0 : Double.valueOf(dormScoreCellValue);
                moralEvaluation.setDormScore(dormScore);
                moralEvaluation.setMateScore(mateScore);
                moralEvaluation.setStuNo(stuNo);
                moralEvaluation.setStuName(stuName);
                moralEvaluation.setTeacherScore(teacherScore);
                moralEvaluations.add(moralEvaluation);
            }
        }
        return moralEvaluations;
    }
}
