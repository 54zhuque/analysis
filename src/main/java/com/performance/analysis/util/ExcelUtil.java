package com.performance.analysis.util;

import com.performance.analysis.common.SystemCode;
import com.performance.analysis.exception.DataReadInException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: Tangwei
 * @Date: 2018/5/29 下午3:51
 * <p>
 * Excel操作
 */
public class ExcelUtil {
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    /**
     * 获取Excel Workbook
     *
     * @param file
     * @return
     * @throws DataReadInException
     * @throws IOException
     */
    public static Workbook getWorkbook(File file) throws DataReadInException, IOException {
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
        return workbook;
    }

    /**
     * 获取cell value
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
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
