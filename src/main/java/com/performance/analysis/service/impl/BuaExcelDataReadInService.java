package com.performance.analysis.service.impl;

import com.performance.analysis.commom.SystemCode;
import com.performance.analysis.dao.MoralEvaluationDao;
import com.performance.analysis.dao.PhysicalEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.MoralEvaluation;
import com.performance.analysis.pojo.PhysicalEvaluation;
import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.DataReadInService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:19
 * <p>
 * BUA Excel数据导入
 */
@Service
public class BuaExcelDataReadInService implements DataReadInService {
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";

    @Autowired
    private PhysicalEvaluationDao physicalEvaluationDao;
    @Autowired
    private MoralEvaluationDao moralEvaluationDao;
    @Autowired
    private StudentDao studentDao;

    /**
     * BUA数据读入
     *
     * @param args 文件路径，读入类型
     * @throws IOException
     * @throws DataReadInException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public void readIn(String... args) throws IOException, DataReadInException {
        File file = new File(args[0]);
        if (file == null) {
            throw new DataReadInException(SystemCode.FILE_NOT_FIND.getMsg());
        }
        String fileName = file.getName();
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            throw new DataReadInException(SystemCode.READIN_MUST_EXCEL.getMsg());
        }
        Workbook workbook = null;
        if (fileName.endsWith(XLS)) {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (fileName.endsWith(XLSX)) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        }
        if (workbook == null) {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
        if (fileName.contains(BuaExcelEnum.PHYSICAL.toString())) {
            List<PhysicalEvaluation> physicalEvaluations = this.readInPhysicalEvaluation(workbook);
            for (PhysicalEvaluation physicalEvaluation : physicalEvaluations) {
                int enrollmentYear = this.getEnrollmentYear(physicalEvaluation.getStuNo());
                Integer grade = BuaAnalyticalRule.getGrade(enrollmentYear);
                Student stu = new Student(physicalEvaluation.getStuNo(), physicalEvaluation.getName(), grade);
                studentDao.addStudent(stu);
                Double[] weights = BuaAnalyticalRule.getPhysicalWeights(grade);
                physicalEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, physicalEvaluation.getCultureScore(),
                        physicalEvaluation.getTrainingScore(), physicalEvaluation.getAdditionalPlus()));
                physicalEvaluationDao.addPhysicalEvaluation(physicalEvaluation);
            }
        } else if (fileName.contains(BuaExcelEnum.MORAL.toString())) {
            List<MoralEvaluation> moralEvaluations = this.readInMoralEvaluation(workbook);
            for (MoralEvaluation moralEvaluation : moralEvaluations) {
                int enrollmentYear = this.getEnrollmentYear(moralEvaluation.getStuNo());
                Integer grade = BuaAnalyticalRule.getGrade(enrollmentYear);
                Student stu = new Student(moralEvaluation.getStuNo(), moralEvaluation.getName(), grade);
                studentDao.addStudent(stu);
                Double[] weights = BuaAnalyticalRule.getMoralWeights();
                moralEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, moralEvaluation.getMateScore(),
                        moralEvaluation.getTeacherScore(), moralEvaluation.getDormScore()));
                moralEvaluationDao.addMoralEvaluation(moralEvaluation);
            }
        } else if (fileName.contains(BuaExcelEnum.MAJOY.toString())) {
            //TODO 专业成绩计算入库

        } else {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
    }

    /**
     * 读入身体素质评分
     *
     * @param workbook
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
                String stuNo = this.getCellValue(row.getCell(0));
                String stuName = this.getCellValue(row.getCell(1));
                String cultureScoreCellValue = this.getCellValue(row.getCell(2));
                Double cultureScore = StringUtils.isEmpty(cultureScoreCellValue) ?
                        0 : Double.valueOf(cultureScoreCellValue);
                String trainingScoreCellValue = this.getCellValue(row.getCell(3));
                Double trainingScore = StringUtils.isEmpty(trainingScoreCellValue) ?
                        0 : Double.valueOf(trainingScoreCellValue);
                String additionalPlusCellValue = this.getCellValue(row.getCell(4));
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

    /**
     * 读入思想素质评分
     *
     * @param workbook
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
                String stuNo = this.getCellValue(row.getCell(0));
                String stuName = this.getCellValue(row.getCell(1));
                String mateScoreCellValue = this.getCellValue(row.getCell(2));
                Double mateScore = StringUtils.isEmpty(mateScoreCellValue) ?
                        0 : Double.valueOf(mateScoreCellValue);
                String teacherScoreCellValue = this.getCellValue(row.getCell(3));
                Double teacherScore = StringUtils.isEmpty(teacherScoreCellValue) ?
                        0 : Double.valueOf(teacherScoreCellValue);
                String dormScoreCellValue = this.getCellValue(row.getCell(4));
                Double dormScore = StringUtils.isEmpty(dormScoreCellValue) ?
                        0 : Double.valueOf(dormScoreCellValue);
                moralEvaluation.setDormScore(dormScore);
                moralEvaluation.setMateScore(mateScore);
                moralEvaluation.setStuNo(stuNo);
                moralEvaluation.setName(stuName);
                moralEvaluation.setTeacherScore(teacherScore);
                moralEvaluations.add(moralEvaluation);
            }
        }
        return moralEvaluations;
    }

    /**
     * 从学号中获取入学年份
     *
     * @param stuNo 学号
     * @return
     */
    private Integer getEnrollmentYear(String stuNo) {
        return Integer.valueOf(stuNo.substring(0, 4));
    }


    /**
     * 获取cell value
     *
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
