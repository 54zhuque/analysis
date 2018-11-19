package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.common.BuaEvaluationEnum;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.dto.StudentScoreDto;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 奖学金评选
 * 计算方式：
 * 特等奖学金---基础性素质分为前2%，大二英语前50%，大三大四CET4
 * 一等奖学金---基础性素质分为前次4%，大二英语前50%，大三大四CET4
 * 二等奖学金---基础性素质分为前次8%，大二英语前50%，大三大四CET4
 * 三等奖学金---基础性素质分为前次6%
 *
 * @author tangwei
 * @since 1.0
 */
@Service
public class ScholarshipEvaluationService implements BuaEvaluationService {
    private static final int MIN_STUDENT_NUM = 50;
    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public List<StudentEvaluationResult> evaluate(BuaEvaluation evaluation) {
        String evaluationResult = evaluation.getEvaluationResult();
        Integer grade = evaluation.getGrade();
        List<StudentEvaluationResult> results = studentEvaluationDao.
                findStudentEvaluationByTypeTwo(evaluationResult, grade);
        if (results != null && results.size() > 0) {
            return results;
        }
        List<StudentEvaluationDto> dtos = studentEvaluationDao.findStudentEvaluationsWithGrade(grade);
        if (dtos == null || dtos.size() == 0) {
            return null;
        }
        //获取通过CET-4列表
        List<String> cet4List = studentEvaluationDao.getEnglishCET4List();
        //英语中位数
        Double englishMedianScore = this.getMedianNum(dtos);
        results = this.getResults(dtos, cet4List, englishMedianScore);
        if (results != null && results.size() > 0) {
            for (StudentEvaluationResult result : results) {
                studentEvaluationDao.addStudentEvaluationResult(result);
            }
        }
        return results;
    }


    /**
     * 排序、分组、过滤
     *
     * @param dtos
     * @return
     */
    private List<StudentEvaluationResult> getResults(List<StudentEvaluationDto> dtos, List<String> cet4List, Double englishMedianScore) {
        if (dtos.size() < MIN_STUDENT_NUM) {
            return null;
        }
        List<StudentScoreDto> studentScoreDtos = this.getSortStudentScores(dtos);
        BigDecimal per1 = new BigDecimal(0.02);
        BigDecimal per2 = per1.add(new BigDecimal(0.04));
        BigDecimal per3 = per2.add(new BigDecimal(0.08));
        BigDecimal per4 = per3.add(new BigDecimal(0.06));
        BigDecimal len = new BigDecimal(studentScoreDtos.size());
        int index0 = len.multiply(per1).intValue();
        int index1 = len.multiply(per2).intValue();
        int index2 = len.multiply(per3).intValue();
        int index3 = len.multiply(per4).intValue();
        List<StudentScoreDto> scores0 = studentScoreDtos.subList(0, index0 + 1);
        List<StudentScoreDto> scores1 = studentScoreDtos.subList(index0 + 1, index1 + 1);
        List<StudentScoreDto> scores2 = studentScoreDtos.subList(index1 + 1, index2 + 1);
        List<StudentScoreDto> scores3 = studentScoreDtos.subList(index2 + 1, index3 + 1);
        List<StudentEvaluationResult> results = new ArrayList<>(studentScoreDtos.size());
        List<StudentEvaluationResult> tmp;
        if (scores0 != null && scores0.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W0.getValue();
            tmp = this.getFilterResults(evaluationResult, scores0, cet4List, englishMedianScore);
            results.addAll(tmp);
        }
        if (scores1 != null && scores1.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W1.getValue();
            tmp = this.getFilterResults(evaluationResult, scores1, cet4List, englishMedianScore);
            results.addAll(tmp);
        }
        if (scores2 != null && scores2.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W2.getValue();
            tmp = this.getFilterResults(evaluationResult, scores2, cet4List, englishMedianScore);
            results.addAll(tmp);
        }
        if (scores3 != null && scores3.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W3.getValue();
            tmp = this.getFilterResults(evaluationResult, scores3, cet4List, englishMedianScore);
            results.addAll(tmp);
        }

        return results;
    }

    /**
     * 整体排序
     *
     * @param dtos 数据
     * @return
     */
    private List<StudentScoreDto> getSortStudentScores(List<StudentEvaluationDto> dtos) {
        List<StudentScoreDto> studentScoreDtos = new ArrayList<>(dtos.size());
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

            StudentScoreDto studentScoreDto = new StudentScoreDto();
            studentScoreDto.setEnglishScore(StringUtils.isEmpty(englishScore) ? 0 : Double.valueOf(englishScore));
            studentScoreDto.setStuGrade(stuGrade);
            studentScoreDto.setStuMajor(stuMajor);
            studentScoreDto.setMajorScore(majorScore);
            studentScoreDto.setMoralScore(moralScore);
            studentScoreDto.setStuName(stuName);
            studentScoreDto.setPhysicalScore(physicalScore);
            studentScoreDto.setStuNo(stuNo);
            studentScoreDto.setExtraScore(extraScore);
            studentScoreDtos.add(studentScoreDto);
        }
        Collections.sort(studentScoreDtos, new Comparator<StudentScoreDto>() {
            @Override
            public int compare(StudentScoreDto o1, StudentScoreDto o2) {
                Double score1 = o1.getFixScore();
                Double score2 = o2.getFixScore();
                return score1 > score2 ? -1 : score1 < score2 ? 1 : 0;
            }
        });
        return studentScoreDtos;
    }

    /**
     * 过滤分组内奖学金
     *
     * @param evaluationResult   奖学金类型
     * @param studentScoreDtos   学生成绩
     * @param cet4List           英语四级
     * @param englishMedianScore 英语中位数
     * @return List<StudentEvaluationResult>
     */
    private List<StudentEvaluationResult> getFilterResults(String evaluationResult, List<StudentScoreDto> studentScoreDtos, List<String> cet4List, Double englishMedianScore) {
        List<StudentEvaluationResult> tmp = new ArrayList<>(studentScoreDtos.size());
        for (StudentScoreDto studentScoreDto : studentScoreDtos) {
            String stuNo = studentScoreDto.getStuNo();
            Integer stuGrade = studentScoreDto.getStuGrade();
            Double englishScore = studentScoreDto.getEnglishScore();
            boolean isCET4 = cet4List != null && cet4List.contains(stuNo);
            boolean isGtMedianScore = englishScore >= englishMedianScore;
            boolean isMet = this.meetEnglishRequirements(stuGrade, isCET4, isGtMedianScore);
            if (isMet) {
                StudentEvaluationResult result = new StudentEvaluationResult();
                BeanUtils.copyProperties(studentScoreDto, result);
                result.setEnglishScore(isCET4 ? "CET4" : String.valueOf(studentScoreDto.getEnglishScore()));
                result.setEvaluationResult(evaluationResult);
                tmp.add(result);
            }
        }
        Collections.sort(tmp, new Comparator<StudentEvaluationResult>() {
            @Override
            public int compare(StudentEvaluationResult o1, StudentEvaluationResult o2) {
                Double extraScore1 = o1.getExtraScore();
                Double extraScore2 = o2.getExtraScore();
                return extraScore1 > extraScore2 ? -1 : extraScore1 < extraScore2 ? 1 : 0;
            }
        });
        return tmp;
    }

    /**
     * 奖学金英语满足要求
     *
     * @param grade           年级
     * @param isCET4          是否过英语四级
     * @param isGtMedianScore 英语成绩是否在班级前50%
     * @return
     */
    private boolean meetEnglishRequirements(Integer grade, boolean isCET4, boolean isGtMedianScore) {
        if (grade == 2) {
            if (!isGtMedianScore) {
                if (!isCET4) {
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
            scores.add(Integer.valueOf(s));
        }
        return BuaAnalyticalRule.getMedianNum(scores);
    }
}
