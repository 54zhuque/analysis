package com.performance.analysis.service.impl;

import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.EnglishEvaluation;
import com.performance.analysis.service.FileDataReadService;
import com.performance.analysis.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

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
    @Override
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
    }

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
        }
        return englishEvaluations;
    }
}
