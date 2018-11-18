package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.pojo.StudentScore;
import com.performance.analysis.service.BuaEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<StudentEvaluationResult> evaluate(BuaEvaluation evaluation) {
        String evaluationResult = evaluation.getEvaluationResult();
        Integer grade = evaluation.getGrade();
        String major = evaluation.getMajor();
        List<StudentEvaluationResult> results = studentEvaluationDao.
                findStudentEvaluationByTypeOne(evaluationResult, grade, major);
        if (results != null && results.size() > 0) {
            return results;
        }
        List<StudentEvaluationDto> dtos = studentEvaluationDao.findStudentEvaluations(grade, major);
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
            String englishScore = dto.getEnglishScore();
            Integer stuGrade = dto.getStuGrade();
            String stuName = dto.getStuName();
            String stuNo = dto.getStuNo();
            String stuMajor = dto.getMajor();

            StudentScore studentScore = new StudentScore();
            studentScore.setEnglishScore(StringUtils.isEmpty(englishScore) ? 0 : Double.valueOf(englishScore));
            studentScore.setGrade(stuGrade);
            studentScore.setMajor(stuMajor);
            studentScore.setMajorFixedScore(majorScore);
            studentScore.setMoralFixedScore(moralScore);
            studentScore.setName(stuName);
            studentScore.setPhysicalFixedScore(physicalScore);
            studentScore.setStuNo(stuNo);
            boolean isClassCadre = classCadre != null && classCadre.contains(stuNo);
            boolean isMetRequirements = this.meetRequirements(studentScore, isClassCadre);
            if (isMetRequirements) {
                studentEvaluationResult = new StudentEvaluationResult();
                studentEvaluationResult.setEvaluationResult(evaluationResult);
                studentEvaluationResult.setMajorScore(majorScore);
                studentEvaluationResult.setMoralScore(moralScore);
                studentEvaluationResult.setPhysicalScore(physicalScore);
                studentEvaluationResult.setEnglishScore(englishScore);
                studentEvaluationResult.setStuName(stuName);
                studentEvaluationResult.setStuGrade(stuGrade);
                studentEvaluationResult.setStuNo(stuNo);
                studentEvaluationDao.addStudentEvaluationResult(studentEvaluationResult);
            }
        }
        //重新查询排序后的结果
        results = studentEvaluationDao.
                findStudentEvaluationByTypeOne(evaluationResult, grade, major);
        return results;
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
        Double physicalFixedScore = studentScore.getPhysicalFixedScore();
        Double moralFixedScore = studentScore.getMoralFixedScore();
        Double majorFixedScore = studentScore.getMajorFixedScore();
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
