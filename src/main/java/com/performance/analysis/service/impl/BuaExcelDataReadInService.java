package com.performance.analysis.service.impl;

import com.alibaba.fastjson.JSON;
import com.performance.analysis.common.SystemCode;
import com.performance.analysis.dao.MajorEvaluationDao;
import com.performance.analysis.dao.MoralEvaluationDao;
import com.performance.analysis.dao.PhysicalEvaluationDao;
import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.*;
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
import java.util.List;

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
    private StudentDao studentDao;
    @Autowired
    private PhysicalEvaluationDao physicalEvaluationDao;
    @Autowired
    private MoralEvaluationDao moralEvaluationDao;
    @Autowired
    private MajorEvaluationDao majorEvaluationDao;


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
                Student stu = new Student(moralEvaluation.getStuNo(), moralEvaluation.getStuName(), grade);
                studentDao.addStudent(stu);
                Double[] weights = BuaAnalyticalRule.getMoralWeights();
                moralEvaluation.setFixScore(BuaAnalyticalRule.getWeightedScore(weights, moralEvaluation.getMateScore(),
                        moralEvaluation.getTeacherScore(), moralEvaluation.getDormScore()));
                moralEvaluationDao.addMoralEvaluation(moralEvaluation);
            }
        } else if (fileName.contains(BuaExcelEnum.MAJOY.toString())) {
            List<MajorEvaluation> majorEvaluations = this.readInMajorEvaluation(workbook);
            for (MajorEvaluation majorEvaluation : majorEvaluations) {
                int enrollmentYear = this.getEnrollmentYear(majorEvaluation.getStuNo());
                Integer grade = BuaAnalyticalRule.getGrade(enrollmentYear);
                Student stu = new Student(majorEvaluation.getStuNo(), majorEvaluation.getStuName(), grade);
                studentDao.addStudent(stu);
                majorEvaluation.setFixScore(this.getMajorWeightedAverageScore(majorEvaluation.getCourseEvaluations()));
                majorEvaluationDao.addMajorEvaluation(majorEvaluation.getStuNo(),
                        JSON.toJSONString(majorEvaluation.getCourseEvaluations()),
                        majorEvaluation.getFixScore());
            }
        } else {
            throw new DataReadInException(SystemCode.READIN_ERROR.getMsg());
        }
    }

    /**
     * 读入身体素质评分
     *
     * @param workbook
     * @return
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
                moralEvaluation.setStuName(stuName);
                moralEvaluation.setTeacherScore(teacherScore);
                moralEvaluations.add(moralEvaluation);
            }
        }
        return moralEvaluations;
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
                String stuNo = this.getCellValue(row.getCell(0));
                String stuName = this.getCellValue(row.getCell(1));
                int lastCellNum = row.getPhysicalNumberOfCells();
                for (int cellNum = 2; cellNum < lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    Double score = StringUtils.isEmpty(this.getCellValue(cell)) ? null
                            : Double.valueOf(this.getCellValue(cell));
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
            String[] course = this.getCourse(this.getCellValue(cell));
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
