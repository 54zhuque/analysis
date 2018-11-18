package com.performance.analysis.service;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.pojo.StudentEvaluationResult;

import java.util.List;

/**
 * 全年级类评选
 *
 * @author tangwei
 * @since 1.0
 */
public interface BuaEvaluationService {
    /**
     * 全年级类评选
     *
     * @param evaluation 评选内容对象
     * @return 评选结果
     */
    List<StudentEvaluationResult> evaluate(BuaEvaluation evaluation);
}
