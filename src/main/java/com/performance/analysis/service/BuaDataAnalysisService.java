package com.performance.analysis.service;

import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.StudentEvaluationResult;

import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午1:54
 * <p>
 * 数据处理分析
 */
public interface BuaDataAnalysisService {
    /**
     * 针对专业年级分析
     *
     * @param majorGrade 专业年级，可从学号前几位获取
     * @return
     * @throws DataReadInException
     */
    List<StudentEvaluationResult> majorGradeAnalysis(String majorGrade) throws DataReadInException;
}
