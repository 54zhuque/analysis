package com.performance.analysis.service.impl;

import com.performance.analysis.dao.ScholarshipEvaluatingDao;
import com.performance.analysis.pojo.ScholarshipEvaluatingResult;
import com.performance.analysis.service.ScholarshipEvaluatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖学金评选过程Service
 *
 * @author tangwei
 * @since 1.0
 */
@Service
public class ScholarshipEvaluatingServiceImpl implements ScholarshipEvaluatingService {
    @Autowired
    private ScholarshipEvaluatingDao scholarshipEvaluatingDao;

    @Override
    public List<ScholarshipEvaluatingResult> getScholarshipEvaluatingResults() {
        return scholarshipEvaluatingDao.findScholarshipEvaluatingResults();
    }

    @Override
    public void updateScholarshipEvaluatingResult2(String evaluation_result2, String stuNo) {
        scholarshipEvaluatingDao.updateScholarshipEvaluatingResult(evaluation_result2, stuNo);
    }

    @Override
    public List<ScholarshipEvaluatingResult> getScholarshipConcludeEvaluatingResults(String evaluationResult) {
        return scholarshipEvaluatingDao.findScholarshipConcludeEvaluatingResults(evaluationResult);
    }
}
