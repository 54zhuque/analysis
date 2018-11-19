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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<MoralEvaluation> moralEvaluations = this.readInMoralEvaluation(workbook);
        for (MoralEvaluation moralEvaluation : moralEvaluations) {
            Student stu = new Student();
            stu.setStuNo(moralEvaluation.getStuNo());
            stu.setGrade(BuaAnalyticalRule.getGrade(moralEvaluation.getStuNo()));
            stu.setName(moralEvaluation.getStuName());
            stu.setMajor(BuaAnalyticalRule.getMajor(moralEvaluation.getStuNo()));
            studentDao.addStudent(stu);
            Double[] weights = BuaAnalyticalRule.getMoralWeights();
            moralEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, moralEvaluation.getMateScore(),
                    moralEvaluation.getTeacherScore(), moralEvaluation.getDormScore()));
            moralEvaluationDao.addMoralEvaluation(moralEvaluation);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataReadInException.class)
    public void readMerge(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<MoralEvaluation> moralEvaluations = this.readInMoralEvaluation(workbook);
        for (MoralEvaluation moralEvaluation : moralEvaluations) {
            String stuNo = moralEvaluation.getStuNo();
            MoralEvaluation evaluation = moralEvaluationDao.findMoralEvaluationByStuNo(stuNo);
            if (evaluation == null) {
                throw new DataReadInException("缺失上学期数据！");
            }
            //计算mate平均分
            Double mateAverageScore = BuaAnalyticalRule
                    .getAverageScore(moralEvaluation.getMateScore(), evaluation.getMateScore());
            //计算teacher平均分
            Double teacherAverageScore = BuaAnalyticalRule
                    .getAverageScore(moralEvaluation.getTeacherScore(), evaluation.getTeacherScore());
            //计算宿舍平均分
            Double dormAverageScore = BuaAnalyticalRule
                    .getAverageScore(moralEvaluation.getDormScore(), evaluation.getDormScore());
            //重新计算加权分
            Double[] weights = BuaAnalyticalRule.getMoralWeights();
            Double fixAverageScore = BuaAnalyticalRule.getWeightedScore(weights, mateAverageScore, teacherAverageScore, dormAverageScore);
            evaluation.setDormScore(dormAverageScore);
            evaluation.setMateScore(mateAverageScore);
            evaluation.setTeacherScore(teacherAverageScore);
            evaluation.setFixScore(fixAverageScore);
            //更新思想素质数据
            moralEvaluationDao.addMoralEvaluation(evaluation);
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
