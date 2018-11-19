package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.pojo.StudentScore;
import com.performance.analysis.service.BuaEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 优秀班干部评选
 * <p>
 * 计算条件：
 * 是班级干部
 * 身体素质分大于75
 * 思想素质分大于85
 * 专业素质分大于75
 *
 * @author tangwei
 * @since 1.0
 */
@Service
public class ClassCadreModelEvaluationService implements BuaEvaluationService {
    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public List<StudentEvaluationResult> evaluate(BuaEvaluation evaluation) {
        String evaluationResult = evaluation.getEvaluationResult();
        Integer grade = evaluation.getGrade();
        String major = evaluation.getMajor();
        List<StudentEvaluationResult> results = studentEvaluationDao.
                findStudentEvaluationByTypeOne(evaluationResult, grade, major);
        if (results != null && results.size() > 0) {
            return results;
        }
        List<StudentEvaluationDto> dtos = studentEvaluationDao.findStudentEvaluationsWithGradMajor(grade, major);
        if (dtos == null || dtos.size() == 0) {
            return null;
        }
        //获取班干部列表
        List<String> classCadre = studentEvaluationDao.getClassCadreList();
        StudentEvaluationResult studentEvaluationResult;
        for (StudentEvaluationDto dto : dtos) {
            Double physicalScore = dto.getPhysicalScore();
            Double moralScore = dto.getMoralScore();
            Double majorScore = dto.getMajorScore();
            Double extraScore = dto.getExtraScore();
            String englishScore = dto.getEnglishScore();
            Integer stuGrade = dto.getStuGrade();
            String stuName = dto.getStuName();
            String stuNo = dto.getStuNo();
            String stuMajor = dto.getMajor();
            Double fixScore = BuaAnalyticalRule.getWeightedScore(this.getWeights(), physicalScore, majorScore, moralScore);

            StudentScore studentScore = new StudentScore();
            studentScore.setEnglishScore(StringUtils.isEmpty(englishScore) ? 0 : Double.valueOf(englishScore));
            studentScore.setStuGrade(stuGrade);
            studentScore.setStuMajor(stuMajor);
            studentScore.setMajorScore(majorScore);
            studentScore.setMoralScore(moralScore);
            studentScore.setStuName(stuName);
            studentScore.setPhysicalScore(physicalScore);
            studentScore.setStuNo(stuNo);
            studentScore.setExtraScore(extraScore);
            studentScore.setFixScore(fixScore);
            boolean isClassCadre = classCadre != null && classCadre.contains(stuNo);
            boolean isMetRequirements = this.meetRequirements(studentScore, isClassCadre);
            if (isMetRequirements) {
                studentEvaluationResult = new StudentEvaluationResult();
                BeanUtils.copyProperties(studentScore, studentEvaluationResult);
                studentEvaluationResult.setEnglishScore(String.valueOf(studentScore.getEnglishScore()));
                studentEvaluationResult.setEvaluationResult(evaluationResult);
                studentEvaluationDao.addStudentEvaluationResult(studentEvaluationResult);
            }
        }
        //重新查询排序后的结果
        results = studentEvaluationDao.
                findStudentEvaluationByTypeOne(evaluationResult, grade, major);
        return results;
    }

    /**
     * 根据比重计算综合素质分
     *
     * @return weights
     */
    private Double[] getWeights() {
        Double[] weights = new Double[]{0.2d, 0.6d, 0.2d};
        return weights;
    }

    /**
     * 是否满足评判优秀班干部
     *
     * @param studentScore 学生成绩
     * @param isClassCadre 是否为班干部
     * @return 是否满足 boolean
     */
    private boolean meetRequirements(StudentScore studentScore, boolean isClassCadre) {
        Double physicalScoreRequire = 75d;
        Double moralScoreRequire = 85d;
        Double majorScoreRequire = 75d;
        Double physicalFixedScore = studentScore.getPhysicalScore();
        Double moralFixedScore = studentScore.getMoralScore();
        Double majorFixedScore = studentScore.getMajorScore();
        //满足是班干部条件
        if (!isClassCadre) {
            return false;
        }
        //身体素质不大于75分不符合
        if (physicalFixedScore <= physicalScoreRequire) {
            return false;
        }
        //思想素质不大于85分不符合
        if (moralFixedScore <= moralScoreRequire) {
            return false;
        }
        //专业素质不大于75分不符合
        if (majorFixedScore <= majorScoreRequire) {
            return false;
        }
        return true;
    }
}
