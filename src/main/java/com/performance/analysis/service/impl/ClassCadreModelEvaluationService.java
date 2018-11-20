package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentScoreDto;
import com.performance.analysis.pojo.CourseEvaluation;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优秀班干部评选，范围全年级
 * <p>
 * 计算条件：
 * 1.各门学科不挂科分数>60
 * 2.学生干部考核结果为优秀
 * 3.身体素质分大于75
 * 4.思想素质分大于85
 * 5.专业素质分大于75
 * <p>
 * 基础素质分计算：
 * 身体素质分20%
 * 思想素质分20%
 * 专业素质分60%
 * <p>
 * 排序：
 * 按发展性素质分排序
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
        List<StudentEvaluationResult> results = studentEvaluationDao.findStudentEvaluationByTypeTwo(evaluationResult, grade);
        if (results != null && results.size() > 0) {
            return results;
        }
        List<StudentScoreDto> dtos = studentEvaluationDao.findStudentEvaluationsWithGrade(grade);
        if (dtos == null || dtos.size() == 0) {
            return null;
        }
        //获取优秀班干部列表
        List<String> classCadre = studentEvaluationDao.getClassCadreList();
        StudentEvaluationResult studentEvaluationResult;
        for (StudentScoreDto dto : dtos) {
            boolean isClassCadre = classCadre != null && classCadre.contains(dto.getStuNo());
            boolean isMet = this.meetRequirements(dto, isClassCadre);
            if (isMet) {
                studentEvaluationResult = new StudentEvaluationResult();
                //基础素质分、综合素质分在studentScoreDto中计算
                BeanUtils.copyProperties(dto, studentEvaluationResult);
                studentEvaluationResult.setEnglishScore(String.valueOf(dto.getEnglishScore()));
                studentEvaluationResult.setEvaluationResult(evaluationResult);
                studentEvaluationDao.addStudentEvaluationResult(studentEvaluationResult);
            }
        }
        //重新查询排序后的结果
        results = studentEvaluationDao.findStudentEvaluationByTypeTwo(evaluationResult, grade);
        return results;
    }

    /**
     * 是否满足评判优秀班干部
     *
     * @param studentScoreDto 学生成绩
     * @param isClassCadre    是否为优秀班干部
     * @return 是否满足 boolean
     */
    private boolean meetRequirements(StudentScoreDto studentScoreDto, boolean isClassCadre) {
        List<CourseEvaluation> courseEvaluations = studentScoreDto.getStuCourse();
        if (courseEvaluations == null) {
            return false;
        }
        Double passScore = 60d;
        for (CourseEvaluation courseEvaluation : courseEvaluations) {
            //满足没有挂科，各科成绩>=60
            if (courseEvaluation.getScore() < passScore) {
                return false;
            }
        }
        Double physicalScoreRequire = 75d;
        Double moralScoreRequire = 85d;
        Double majorScoreRequire = 75d;
        Double physicalFixedScore = studentScoreDto.getPhysicalScore();
        Double moralFixedScore = studentScoreDto.getMoralScore();
        Double majorFixedScore = studentScoreDto.getMajorScore();
        //满足是优秀班干部条件
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
