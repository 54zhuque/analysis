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

import java.util.ArrayList;
import java.util.List;

/**
 * 三好学生评选，范围全年级
 * <p>
 * 计算条件：
 * 1.没有挂科（各科成绩>=60）
 * 2.身体素质分大于80
 * 3.思想素质分大于85
 * 4.专业素质分大于85
 * 5.二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
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
public class StudentModelEvaluationService implements BuaEvaluationService {
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
        //获取通过CET-4列表
        List<String> cet4List = studentEvaluationDao.getEnglishCET4List();
        Double englishMedianScore = this.getMedianNum(dtos);
        //筛选三好学生
        StudentEvaluationResult studentEvaluationResult;
        for (StudentScoreDto dto : dtos) {
            boolean isCET4 = cet4List != null && cet4List.contains(dto.getStuNo());
            boolean isMet = this.meetRequirements(dto, isCET4, englishMedianScore);
            if (isMet) {
                studentEvaluationResult = new StudentEvaluationResult();
                //基础素质分、综合素质分在studentScoreDto中计算
                BeanUtils.copyProperties(dto, studentEvaluationResult);
                studentEvaluationResult.setEnglishScore(isCET4 ? "CET4" : String.valueOf(dto.getEnglishScore()));
                studentEvaluationResult.setEvaluationResult(evaluationResult);
                studentEvaluationDao.addStudentEvaluationResult(studentEvaluationResult);
            }
        }
        //重新查询排序后的结果
        results = studentEvaluationDao.findStudentEvaluationByTypeTwo(evaluationResult, grade);
        return results;
    }

    /**
     * 是否满足评判三好学生条件
     *
     * @param studentScoreDto    学生成绩
     * @param isCET4             是否过英语四级
     * @param englishMedianScore 英语中位数
     * @return 是否满足 boolean
     */
    private boolean meetRequirements(StudentScoreDto studentScoreDto, boolean isCET4, Double englishMedianScore) {
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
        Double physicalScoreRequire = 80d;
        Double moralScoreRequire = 85d;
        Double majorScoreRequire = 85d;
        Double physicalFixedScore = studentScoreDto.getPhysicalScore();
        Double moralFixedScore = studentScoreDto.getMoralScore();
        Double majorFixedScore = studentScoreDto.getMajorScore();
        Double englishScore = studentScoreDto.getEnglishScore();
        //身体素质不大于80分不符合
        if (physicalFixedScore <= physicalScoreRequire) {
            return false;
        }
        //思想素质不大于85分不符合
        if (moralFixedScore <= moralScoreRequire) {
            return false;
        }
        //专业素质不大于85分不符合
        if (majorFixedScore <= majorScoreRequire) {
            return false;
        }
        Integer grade = studentScoreDto.getStuGrade();
        //二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
        if (grade == 2) {
            if (!isCET4) {
                if (englishScore < englishMedianScore) {
                    return false;
                }
            }
        } else if (grade == 3) {
            if (!isCET4) {
                return false;
            }
        } else if (grade == 4) {
            if (!isCET4) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获取英语成绩中位数
     *
     * @param dtos 数据对象
     * @return 英语成绩中位数
     */
    private Double getMedianNum(List<StudentScoreDto> dtos) {
        List<Integer> scores = new ArrayList<>(dtos.size());
        for (StudentScoreDto dto : dtos) {
            int score = dto.getEnglishScore().intValue();
            scores.add(score);
        }
        return BuaAnalyticalRule.getMedianNum(scores);
    }
}
