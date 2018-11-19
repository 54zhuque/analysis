package com.performance.analysis.service.impl;

import com.performance.analysis.dao.*;
import com.performance.analysis.pojo.*;
import com.performance.analysis.service.VariousGradeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * analysis
 * Date: 2018/6/20
 * Time: 18:54
 *
 * @author duandoudou
 */
@Service
public class VariousGradeServiceImpl implements VariousGradeService {

    @Autowired
    private EnglishEvaluationDao englishEvaluationDao;
    @Autowired
    private MajorEvaluationDao majorEvaluationDao;
    @Autowired
    private MoralEvaluationDao moralEvaluationDao;
    @Autowired
    private PhysicalEvaluationDao physicalEvaluationDao;
    @Autowired
    private ExtraEvaluationDao extraEvaluationDao;
    @Autowired
    private CadreEvaluationDao cadreEvaluationDao;

    @Override
    public List<EnglishEvaluation> listEnglishEvaluation() {
        return englishEvaluationDao.listEnglishEvaluation();
    }

    @Override
    public List<MajorEvaluation> listMajorEvaluation() {
        return majorEvaluationDao.listMajorEvaluation();
    }

    @Override
    public List<MoralEvaluation> listMoralEvaluation() {
        return moralEvaluationDao.listMoralEvaluation();
    }

    @Override
    public List<PhysicalEvaluation> listPhysicalEvaluation() {
        return physicalEvaluationDao.listPhysicalEvaluation();
    }

    @Override
    public List<ExtraEvaluation> listExtraEvaluation() {
        return extraEvaluationDao.listExraEvaluation();
    }

    @Override
    public List<ClassCadre> listCadreEvaluation() {
        return cadreEvaluationDao.listCadreEvaluation();
    }

}
