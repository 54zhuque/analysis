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

import java.util.ArrayList;
import java.util.List;

/**
 * 三好学生评选
 * <p>
 * 计算条件：
 * 身体素质分大于80
 * 思想素质分大于85
 * 专业素质分大于85
 * 一年级学生英语成绩在专业50%以上,二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
 * <p>
 * 排序比重：
 * 身体素质分20%
 * 思想素质分20%
 * 专业素质分60%
 *
 * @author tangwei
 * @since 1.0
 */
@Service
public class StudentModelEvaluationService implements BuaEvaluationService {
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
        //获取通过CET-4列表
        List<String> cet4List = studentEvaluationDao.getEnglishCET4List();
        Double englishMedianScore = this.getMedianNum(dtos);
        //筛选三好学生
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
            boolean isCET4 = cet4List != null && cet4List.contains(stuNo);
            boolean isMetRequirements = this.meetRequirements(studentScore, isCET4, englishMedianScore);
            if (isMetRequirements) {
                studentEvaluationResult = new StudentEvaluationResult();
                Double fixScore = BuaAnalyticalRule.getWeightedScore(this.getWeights(), physicalScore, majorScore, moralScore);
                String englishFixedScore = isCET4 ? "CET4" : englishScore;
                studentEvaluationResult.setEvaluationResult(evaluationResult);
                studentEvaluationResult.setMajorScore(majorScore);
                studentEvaluationResult.setMoralScore(moralScore);
                studentEvaluationResult.setPhysicalScore(physicalScore);
                studentEvaluationResult.setEnglishScore(englishFixedScore);
                studentEvaluationResult.setStuName(stuName);
                studentEvaluationResult.setStuGrade(stuGrade);
                studentEvaluationResult.setStuNo(stuNo);
                studentEvaluationResult.setFixScore(fixScore);
                studentEvaluationDao.addStudentEvaluationResult(studentEvaluationResult);
            }
        }
        //重新查询排序后的结果
        results = studentEvaluationDao.
                findStudentEvaluationByTypeOne(evaluationResult, grade, major);
        return results;
    }

    /**
     * 获取三好学生排序权重
     *
     * @return weights
     */
    private Double[] getWeights() {
        Double[] weights = new Double[]{0.2d, 0.6d, 0.2d};
        return weights;
    }

    /**
     * 是否满足评判三好学生条件
     *
     * @param studentScore       学生成绩
     * @param isCET4             是否过英语四级
     * @param englishMedianScore 英语中位数
     * @return 是否满足 boolean
     */
    private boolean meetRequirements(StudentScore studentScore, boolean isCET4, Double englishMedianScore) {
        Double physicalScoreRequire = 80d;
        Double moralScoreRequire = 85d;
        Double majorScoreRequire = 85d;
        Integer grade = studentScore.getGrade();
        Double physicalFixedScore = studentScore.getPhysicalFixedScore();
        Double moralFixedScore = studentScore.getMoralFixedScore();
        Double majorFixedScore = studentScore.getMajorFixedScore();
        Double englishScore = studentScore.getEnglishScore();
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
        //一年级学生英语成绩在专业50%以上,二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
        if (grade == 1) {
            if (englishScore <= englishMedianScore) {
                return false;
            }
        } else if (grade == 2) {
            if (englishScore <= englishMedianScore) {
                if (!isCET4) {
                    return false;
                }
            }
        } else {
            if (!isCET4) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取英语成绩中位数
     *
     * @param dtos 数据对象
     * @return 英语成绩中位数
     */
    private Double getMedianNum(List<StudentEvaluationDto> dtos) {
        List<Integer> scores = new ArrayList<>(dtos.size());
        for (StudentEvaluationDto dto : dtos) {
            String s = dto.getEnglishScore();
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            Integer score = Integer.valueOf(s);
            scores.add(score);
        }
        return BuaAnalyticalRule.getMedianNum(scores);
    }
}
