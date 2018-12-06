package com.performance.analysis.controller;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.common.BuaEvaluationEnum;
import com.performance.analysis.common.SystemCode;
import com.performance.analysis.common.SystemResponse;
import com.performance.analysis.pojo.ScholarshipEvaluatingResult;
import com.performance.analysis.service.BuaEvaluationService;
import com.performance.analysis.service.ScholarshipEvaluatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 奖学金评选过程
 *
 * @author tangwei
 * @since 1.0
 */
@RestController
public class ScholarshipEvaluatingController {
    @Autowired
    private BuaEvaluationService scholarshipEvaluationService;
    @Autowired
    private ScholarshipEvaluatingService scholarshipEvaluatingService;

    /**
     * 修改奖学金评选等级
     *
     * @param stuNo  学号
     * @param result 奖学金等级
     * @return
     */
    @PostMapping("/bua/scholarship/evaluating/{stuNo}/result/{result}")
    public SystemResponse updateScholarshipEvaluatingResult2(@PathVariable String stuNo, @PathVariable String result) {
        SystemResponse response = new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
        scholarshipEvaluatingService.updateScholarshipEvaluatingResult2(result, stuNo);
        return response;
    }

    /**
     * 获取的奖学金数据
     *
     * @param result 奖学金等级
     * @return
     */
    @GetMapping("/bua/scholarship/evaluating/results/{result}")
    public SystemResponse getScholarshipConcludeEvaluatingResults(@PathVariable String result) {
        SystemResponse response = new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
        BuaEvaluation evaluation = new BuaEvaluation();
        evaluation.setEvaluationResult(BuaEvaluationEnum.SCHOLARSHIP.getValue());
        scholarshipEvaluationService.evaluate(evaluation);
        result = result.toUpperCase();
        List<ScholarshipEvaluatingResult> results = scholarshipEvaluatingService.getScholarshipConcludeEvaluatingResults(result);
        response.setData(results);
        return response;
    }

    /**
     * 提交奖学金最终结果
     *
     * @return
     */
    @GetMapping("/bua/scholarship/evaluated")
    public SystemResponse evaluatedResults() {
        SystemResponse response = new SystemResponse(SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMsg());
        scholarshipEvaluatingService.evaluatedResults();
        return response;
    }
}
