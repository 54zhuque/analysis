package com.performance.analysis.service.impl;

import com.performance.analysis.exception.DataReadInException;
import sun.tools.tree.DoubleExpression;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午12:05
 * <p>
 * BUA计算规则
 */
public class BuaAnalyticalRule {

    private final static int ENROLLMENT_MONTH = 9;
    private final static int ENROLLMENT_DAY = 7;

    /**
     * 计算年级，当前年份与入学年份差值，
     * 若月大于入学月日，最新批学生入学，差值需+1，否则+0
     *
     * @param enrollmentYear
     * @return
     */
    public static Integer getGrade(int enrollmentYear) throws DataReadInException {
        Integer grade;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        grade = year - enrollmentYear;
        if (grade > 4) {
            throw new DataReadInException("Can not rather than 4");
        }
        if (month > ENROLLMENT_MONTH) {
            grade += 1;
        } else if (month < ENROLLMENT_MONTH) {
            grade += 0;
        } else {
            if (day < ENROLLMENT_DAY) {
                grade += 0;
            } else {
                grade += 1;
            }
        }
        return grade;
    }

    /**
     * 计算加权分
     *
     * @param weights
     * @param scores
     * @return
     * @throws DataReadInException
     */
    public static Double getWeightedScore(Double[] weights, Double... scores) throws DataReadInException {
        if (weights.length != scores.length) {
            throw new DataReadInException("It must be the same length");
        }
        BigDecimal weightedScore = new BigDecimal(0);
        int len = weights.length;
        for (int i = 0; i < len; i++) {
            BigDecimal weight = new BigDecimal(weights[i]);
            BigDecimal score = new BigDecimal(scores[i]);
            weightedScore = weightedScore.add(score.multiply(weight));
        }
        return weightedScore.doubleValue();
    }
}
