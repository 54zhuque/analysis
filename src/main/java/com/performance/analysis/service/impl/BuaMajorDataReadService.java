package com.performance.analysis.service.impl;

import com.alibaba.fastjson.JSON;
import com.performance.analysis.common.SystemCode;
import com.performance.analysis.dao.MajorEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.CourseEvaluation;
import com.performance.analysis.pojo.MajorEvaluation;
import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.FileDataReadService;
import com.performance.analysis.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
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
 * @Date: 2018/5/29 下午4:03
 * <p>
 * 学科专业成绩读入
 */
@Service
public class BuaMajorDataReadService implements FileDataReadService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private MajorEvaluationDao majorEvaluationDao;

    @Override
    public void read(File file) throws IOException, DataReadInException {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        List<MajorEvaluation> majorEvaluations = this.readInMajorEvaluation(workbook);
        for (MajorEvaluation majorEvaluation : majorEvaluations) {
            Integer grade = BuaAnalyticalRule.getGrade(majorEvaluation.getStuNo());
            Student stu = new Student(majorEvaluation.getStuNo(), majorEvaluation.getStuName(), grade);
            studentDao.addStudent(stu);
            majorEvaluation.setFixScore(this.getMajorWeightedAverageScore(majorEvaluation.getCourseEvaluations()));
            majorEvaluationDao.addMajorEvaluation(majorEvaluation.getStuNo(),
                    JSON.toJSONString(majorEvaluation.getCourseEvaluations()),
                    majorEvaluation.getFixScore());
        }
    }

    /**
     * 读入专业素质评分
     *
     * @param workbook
     * @return
     */
    private List<MajorEvaluation> readInMajorEvaluation(Workbook workbook) {
        List<MajorEvaluation> majorEvaluations = new ArrayList<>(30);
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
            List<CourseEvaluation> courseEvaluations = this.getCourse(firstRow);
            MajorEvaluation majorEvaluation;
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                List<CourseEvaluation> copyCourseEvaluations = this.getCourseCopy(courseEvaluations);
                majorEvaluation = new MajorEvaluation();
                String stuNo = ExcelUtil.getCellValue(row.getCell(0));
                String stuName = ExcelUtil.getCellValue(row.getCell(1));
                int lastCellNum = row.getPhysicalNumberOfCells();
                for (int cellNum = 2; cellNum < lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    Double score = StringUtils.isEmpty(ExcelUtil.getCellValue(cell)) ? null
                            : Double.valueOf(ExcelUtil.getCellValue(cell));
                    copyCourseEvaluations.get(cellNum - 2).setScore(score);
                }
                majorEvaluation.setCourseEvaluations(copyCourseEvaluations);
                majorEvaluation.setStuNo(stuNo);
                majorEvaluation.setStuName(stuName);
                majorEvaluations.add(majorEvaluation);
            }
        }
        return majorEvaluations;
    }

    /**
     * 获取Course
     *
     * @param titleRow 标题行
     * @return
     */
    private List<CourseEvaluation> getCourse(Row titleRow) {
        List<CourseEvaluation> courseEvaluations = new ArrayList<>(20);
        int lastCellNum = titleRow.getPhysicalNumberOfCells();
        CourseEvaluation courseEvaluation;
        for (int cellNum = 2; cellNum < lastCellNum; cellNum++) {
            courseEvaluation = new CourseEvaluation();
            Cell cell = titleRow.getCell(cellNum);
            String[] course = this.getCourse(ExcelUtil.getCellValue(cell));
            courseEvaluation.setName(course[0]);
            courseEvaluation.setCredit(Double.valueOf(course[1]));
            courseEvaluations.add(courseEvaluation);
        }
        return courseEvaluations;
    }

    /**
     * Course 深copy
     *
     * @param courseEvaluations
     * @return
     */
    private List<CourseEvaluation> getCourseCopy(List<CourseEvaluation> courseEvaluations) {
        if (courseEvaluations == null || courseEvaluations.size() == 0) {
            return null;
        }
        List<CourseEvaluation> copyCourseEvaluations = new ArrayList<>(courseEvaluations.size());
        for (CourseEvaluation courseEvaluation : courseEvaluations) {
            CourseEvaluation copyCourseEvaluation = new CourseEvaluation();
            copyCourseEvaluation.setScore(courseEvaluation.getScore());
            copyCourseEvaluation.setCredit(courseEvaluation.getCredit());
            copyCourseEvaluation.setName(courseEvaluation.getName());
            copyCourseEvaluations.add(copyCourseEvaluation);
        }
        return copyCourseEvaluations;
    }

    /**
     * 计算专业加权平均分
     *
     * @param courseEvaluations
     * @return
     */
    private Double getMajorWeightedAverageScore(List<CourseEvaluation> courseEvaluations) throws DataReadInException {
        if (courseEvaluations == null || courseEvaluations.size() == 0) {
            return null;
        }
        List<Double> credits = new ArrayList<>(20);
        List<Double> scores = new ArrayList<>(20);
        for (int i = 0; i < courseEvaluations.size(); i++) {
            CourseEvaluation courseEvaluation = courseEvaluations.get(i);
            if (courseEvaluation.getScore() == null) {
                continue;
            }
            credits.add(courseEvaluation.getCredit());
            scores.add(courseEvaluation.getScore());
        }
        if (credits.size() != scores.size()) {
            throw new DataReadInException(SystemCode.READIN_SAME_LENGTH.getMsg());
        }
        return BuaAnalyticalRule.getWeightedAverageScore(credits.toArray(new Double[credits.size()]),
                scores.toArray(new Double[scores.size()]));
    }

    /**
     * 获取课程截取信息
     *
     * @param s
     * @return
     */
    private String[] getCourse(String s) {
        String[] courseBase = new String[2];
        String s1 = s.substring(0, s.indexOf("["));
        String s2 = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
        courseBase[0] = s1.trim();
        courseBase[1] = s2.startsWith(".") ? "0" + s2 : s2;
        return courseBase;
    }
}
