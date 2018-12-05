package com.performance.analysis.service.impl;

import com.performance.analysis.dao.ScholarshipEvaluatingDao;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.pojo.ScholarshipEvaluatingResult;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.ScholarshipEvaluatingService;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    public void updateScholarshipEvaluatingResult2(String evaluation_result2, String stuNo) {
        scholarshipEvaluatingDao.updateScholarshipEvaluatingResult(evaluation_result2, stuNo);
    }

    @Override
    public List<ScholarshipEvaluatingResult> getScholarshipConcludeEvaluatingResults(Integer stuGrade, String evaluationResult) {
        return scholarshipEvaluatingDao.findScholarshipConcludeEvaluatingResults(stuGrade, evaluationResult);
    }

    @Override
    public void evaluatedResults(Integer stuGrade) {
        List<ScholarshipEvaluatingResult> results = scholarshipEvaluatingDao.findScholarshipEvaluatingResults(stuGrade);
        StudentEvaluationResult result;
        for (ScholarshipEvaluatingResult evaluatingResult : results) {
            result = new StudentEvaluationResult();
            BeanUtils.copyProperties(evaluatingResult, result);
            result.setEvaluationResult(evaluatingResult.getEvaluationResult2());
            studentEvaluationDao.addStudentEvaluationResult(result);
        }
    }
}
