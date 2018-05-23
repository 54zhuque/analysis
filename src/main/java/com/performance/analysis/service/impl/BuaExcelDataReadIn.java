package com.performance.analysis.service.impl;

import com.performance.analysis.dao.PhysicalEvaluationDao;
import com.performance.analysis.pojo.MoralEvaluation;
import com.performance.analysis.pojo.PhysicalEvaluation;
import com.performance.analysis.service.DataReadIn;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/23 下午2:19
 * <p>
 * 农学院Excel数据导入
 */
@Service
public class BuaExcelDataReadIn implements DataReadIn {
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";

    @Autowired
    private PhysicalEvaluationDao physicalEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public void readIn(String... args) throws IOException {
        File file = new File(args[0]);
        if (file == null) {
            return;
        }
        String fileName = file.getName();
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            return;
        }
        Workbook workbook = null;
        if (fileName.endsWith(XLS)) {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (fileName.endsWith(XLSX)) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        }
        if (workbook != null) {
            readInPhysicalEvaluation(workbook);
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
            PhysicalEvaluation physicalEvaluation = null;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                physicalEvaluation = new PhysicalEvaluation();
                String stuNo = this.getCellValue(row.getCell(0));
                Double cultureScore = Double.valueOf(this.getCellValue(row.getCell(2)));
                Double trainingScore = Double.valueOf(this.getCellValue(row.getCell(3)));
                Double additionalPlus = Double.valueOf(this.getCellValue(row.getCell(4)));
                physicalEvaluation.setAdditionalPlus(additionalPlus);
                physicalEvaluation.setCultureScore(cultureScore);
                physicalEvaluation.setStuNo(stuNo);
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
            MoralEvaluation moralEvaluation = null;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                moralEvaluation = new MoralEvaluation();
                String stuNo = this.getCellValue(row.getCell(0));
                Double mateScore = Double.valueOf(this.getCellValue(row.getCell(2)));
                Double teacherScore = Double.valueOf(this.getCellValue(row.getCell(3)));
                Double dormScore = Double.valueOf(this.getCellValue(row.getCell(4)));
                moralEvaluation.setDormScore(dormScore);
                moralEvaluation.setMateScore(mateScore);
                moralEvaluation.setStuNo(stuNo);
                moralEvaluation.setTeacherScore(teacherScore);
                moralEvaluations.add(moralEvaluation);
            }
        }
        return moralEvaluations;
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
