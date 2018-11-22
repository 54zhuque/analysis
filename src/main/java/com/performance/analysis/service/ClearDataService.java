package com.performance.analysis.service;

import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * analysis
 * User: Administrator
 * Date: 2018/5/27
 * Time: 12:26
 * @author Administrator
 */
@Service
public class ClearDataService {

    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    public void clearStudentEvaluation(){

        studentEvaluationDao.clearStudentEvaluation();
    }
    public void clearAllData(){

        studentEvaluationDao.clearStudentEvaluation();
        studentEvaluationDao.clearClassCadre();
        studentEvaluationDao.clearEnglishCet4();
        studentEvaluationDao.clearExtraEvaluation();
        studentEvaluationDao.clearMajorEvaluation();
        studentEvaluationDao.clearMoralEvaluation();
        studentEvaluationDao.clearPhysicalEvaluation();
        studentEvaluationDao.clearStudent();
        studentEvaluationDao.clearEnglishEvaluation();
        studentEvaluationDao.clearSqliteSequence();
    }
}
