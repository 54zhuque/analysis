package com.performance.analysis.service.impl;

import com.performance.analysis.common.SystemCode;
import com.performance.analysis.exception.DataReadInException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/24 下午12:05
 * <p>
 * BUA计算规则
 */
public class BuaAnalyticalRule {
    private final static int ENROLLMENT_MONTH = 9;
    private final static int ENROLLMENT_DAY = 7;
    private final static int GRADE_ONE = 1;
    private final static int GRADE_TWO = 2;
    private final static int GRADE_THREE = 3;


    /**
     * 计算年级，当前年份与入学年份差值，
     * 若月大于入学月日，最新批学生入学，差值需+1，否则+0
     *
     * @param stuNo 学号
     * @return
     * @throws DataReadInException
     */
    public static Integer getGrade(String stuNo) throws DataReadInException {
        //截取入学年份
        int enrollmentYear = Integer.valueOf(stuNo.substring(0, 4));
        Integer grade;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        grade = year - enrollmentYear;
        if (grade > 4) {
            throw new DataReadInException(SystemCode.READIN_GRADE_RATHERTHAN.getMsg());
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
     * 获取专业，从学号中截取
     *
     * @param stuNo 学号
     * @return
     */
    public static String getMajor(String stuNo) {
        return stuNo.substring(4, 8);
    }

    /**
     * 获取平均值
     *
     * @param values
     * @return
     */
    public static Double getAverageScore(Double... values) {
        BigDecimal averageScore = new BigDecimal(0);
        int len = values.length;
        for (int i = 0; i < len; i++) {
            BigDecimal value = new BigDecimal(values[i]);
            averageScore = averageScore.add(value);
        }
        return averageScore.divide(new BigDecimal(len), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算加权分
     *
     * @param weights
     * @param values
     * @return
     * @throws DataReadInException
     */
    public static Double getWeightedScore(Double[] weights, Double... values) {
        BigDecimal weightedScore = new BigDecimal(0);
        int len = weights.length;
        for (int i = 0; i < len; i++) {
            BigDecimal weight = new BigDecimal(weights[i]);
            BigDecimal value = new BigDecimal(values[i]);
            weightedScore = weightedScore.add(value.multiply(weight));
        }
        return weightedScore.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算加权平均分 H=∑(value*weight)/∑(weight)
     *
     * @param weights
     * @param values
     * @return
     * @throws DataReadInException
     */
    public static Double getWeightedAverageScore(Double[] weights, Double[] values) throws DataReadInException {
        if (weights.length != values.length) {
            throw new DataReadInException(SystemCode.READIN_SAME_LENGTH.getMsg());
        }
        BigDecimal x1 = new BigDecimal(0);
        BigDecimal x2 = new BigDecimal(0);
        int len = weights.length;
        for (int i = 0; i < len; i++) {
            BigDecimal weight = new BigDecimal(weights[i]);
            BigDecimal value = new BigDecimal(values[i]);
            x1 = x1.add(value.multiply(weight));
            x2 = x2.add(weight);
        }
        return x1.divide(x2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取身体素质权重比例(年级区分)
     *
     * @param grade 年级
     * @return weights
     */
    public static Double[] getPhysicalWeights(int grade) {
        Double[] weights = new Double[3];
        if (grade == GRADE_ONE) {
            weights[0] = 0.4d;
            weights[1] = 0.1d;
            weights[2] = 0.5d;
        } else if (grade == GRADE_TWO) {
            weights[0] = 0.4d;
            weights[1] = 0.2d;
            weights[2] = 0.4d;
        } else if (grade == GRADE_THREE) {
            weights[0] = 0.6d;
            weights[1] = 0.4d;
            weights[2] = 0.0d;
        } else {
            weights[0] = 0.0d;
            weights[1] = 0.0d;
            weights[2] = 0.0d;
        }
        return weights;
    }

    /**
     * 获取思想素质权重比例(无年级区分)
     *
     * @return weights
     */
    public static Double[] getMoralWeights() {
        Double[] weights = new Double[]{0.4d, 0.3d, 0.3d};
        return weights;
    }


    /**
     * 获取中位数
     *
     * @param nums 数字列表
     * @return double 中位数
     */
    public static Double getMedianNum(List<Integer> nums) {
        Double medianNum;
        LinkedList<Integer> sortList = new LinkedList<>();
        for (Integer num : nums) {
            if (sortList.size() == 0 || num < sortList.getFirst()) {
                sortList.addFirst(num);
            } else {
                int len = sortList.size();
                for (int i = 0; i < len; i++) {
                    //存在比列表中数据小的值，选取合适位置插入
                    if (num < sortList.get(i)) {
                        sortList.add(i, num);
                        break;
                    }
                    if (i == len - 1) {
                        sortList.addLast(num);
                    }
                }
            }
        }
        if (sortList.size() == 0) {
            return null;
        }
        //奇数偶数
        boolean even = sortList.size() % 2 == 0;
        if (even) {
            int index = sortList.size() / 2;
            medianNum = Double.valueOf(sortList.get(index - 1) + sortList.get(index)) / 2;
        } else {
            medianNum = Double.valueOf(sortList.get((sortList.size() + 1) / 2 - 1));
        }
        return medianNum;
    }
}
