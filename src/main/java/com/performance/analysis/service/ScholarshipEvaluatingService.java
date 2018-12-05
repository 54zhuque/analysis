package com.performance.analysis.service;

import com.performance.analysis.pojo.ScholarshipEvaluatingResult;

import java.util.List;

/**
 * 奖学金评选过程Service
 *
 * @author tangwei
 * @since 1.0
 */
public interface ScholarshipEvaluatingService {

    /**
     * 更新result2
     *
     * @param evaluation_result2
     * @param stuNo
     */
    void updateScholarshipEvaluatingResult2(String evaluation_result2, String stuNo);

    /**
     * 获取奖学金初步评选归纳后结果
     *
     * @param evaluationResult 奖学金类型
     * @return
     */
    List<ScholarshipEvaluatingResult> getScholarshipConcludeEvaluatingResults(String evaluationResult);
}
