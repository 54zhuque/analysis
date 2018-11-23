package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluation;
import com.performance.analysis.common.BuaEvaluationEnum;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 奖学金评选，范围全年级
 * 计算方式：
 * 1.没有挂科
 * 2.身体素质得分60分以上
 * 3.思想道德素质在75以上
 * 4.特等奖学金---基础性素质分为前2%，大二英语前50%，大三大四CET4
 * 5.一等奖学金---基础性素质分为前次4%，大二英语前50%，大三大四CET4
 * 6.二等奖学金---基础性素质分为前次8%，大二英语前50%，大三大四CET4
 * 7.三等奖学金---基础性素质分为前次6%
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
public class ScholarshipEvaluationService implements BuaEvaluationService {
    private static final int MIN_STUDENT_NUM = 50;
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
    private List<StudentEvaluationResult> getResults(List<StudentScoreDto> dtos, List<String> cet4List, Double englishMedianScore) {
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
        //取2%数据块
        List<StudentScoreDto> scores0 = studentScoreDtos.subList(0, index0 + 1);
        //取次4%数据块
        List<StudentScoreDto> scores1 = studentScoreDtos.subList(index0 + 1, index1 + 1);
        //取次8%数据块
        List<StudentScoreDto> scores2 = studentScoreDtos.subList(index1 + 1, index2 + 1);
        //取次6%数据块
        List<StudentScoreDto> scores3 = studentScoreDtos.subList(index2 + 1, index3 + 1);
        List<StudentEvaluationResult> results = new ArrayList<>(index3);
        List<StudentEvaluationResult> tmp;
        //特等奖学金
        if (scores0 != null && scores0.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W0.getValue();
            //满足filter条件1
            tmp = this.getFilterResults1(evaluationResult, scores0, cet4List, englishMedianScore);
            tmp = this.getSortResults(tmp);
            results.addAll(tmp);
        }
        //一等奖学金
        if (scores1 != null && scores1.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W1.getValue();
            //满足filter条件1
            tmp = this.getFilterResults1(evaluationResult, scores1, cet4List, englishMedianScore);
            tmp = this.getSortResults(tmp);
            results.addAll(tmp);
        }
        //二等奖学金
        if (scores2 != null && scores2.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W2.getValue();
            //满足filter条件1
            tmp = this.getFilterResults1(evaluationResult, scores2, cet4List, englishMedianScore);
            tmp = this.getSortResults(tmp);
            results.addAll(tmp);
        }
        //三等奖学金
        if (scores3 != null && scores3.size() > 0) {
            String evaluationResult = BuaEvaluationEnum.SCHOLARSHIP_W3.getValue();
            //满足filter条件2
            tmp = this.getFilterResults2(evaluationResult, scores3);
            tmp = this.getSortResults(tmp);
            for (StudentEvaluationResult result : tmp) {
                boolean isCET4 = cet4List != null && cet4List.contains(result.getStuNo());
                //处理过CET4的学生
                result.setEnglishScore(isCET4 ? "CET4" : String.valueOf(result.getEnglishScore()));
            }
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
    private List<StudentScoreDto> getSortStudentScores(List<StudentScoreDto> dtos) {
        Collections.sort(dtos, new Comparator<StudentScoreDto>() {
            @Override
            public int compare(StudentScoreDto o1, StudentScoreDto o2) {
                Double score1 = o1.getBasicScore();
                Double score2 = o2.getBasicScore();
                return score1 > score2 ? -1 : score1 < score2 ? 1 : 0;
            }
        });
        return dtos;
    }

    /**
     * 过滤分组内奖学金条件1，针对特等、一等、二等
     *
     * @param evaluationResult   奖学金类型
     * @param studentScoreDtos   学生成绩
     * @param cet4List           英语四级
     * @param englishMedianScore 英语中位数
     * @return List<StudentEvaluationResult>
     */
    private List<StudentEvaluationResult> getFilterResults1(String evaluationResult, List<StudentScoreDto> studentScoreDtos, List<String> cet4List, Double englishMedianScore) {
        List<StudentEvaluationResult> results = new ArrayList<>(studentScoreDtos.size());
        for (StudentScoreDto studentScoreDto : studentScoreDtos) {
            String stuNo = studentScoreDto.getStuNo();
            Integer stuGrade = studentScoreDto.getStuGrade();
            List<CourseEvaluation> courseEvaluations = studentScoreDto.getStuCourse();
            Double englishScore = studentScoreDto.getEnglishScore();
            boolean isCET4 = cet4List != null && cet4List.contains(stuNo);
            boolean isGtMedianScore = englishScore >= englishMedianScore;
            boolean isMetScore = this.meetScoreRequirements(courseEvaluations);
            boolean isMetEnglish = this.meetEnglishRequirements(stuGrade, isCET4, isGtMedianScore);
            if (isMetScore && isMetEnglish) {
                StudentEvaluationResult result = new StudentEvaluationResult();
                BeanUtils.copyProperties(studentScoreDto, result);
                result.setEnglishScore(isCET4 ? "CET4" : String.valueOf(studentScoreDto.getEnglishScore()));
                result.setEvaluationResult(evaluationResult);
                results.add(result);
            }
        }
        return results;
    }

    /**
     * 过滤分组内奖学金条件2，针对三等
     *
     * @param evaluationResult 奖学金类型
     * @param studentScoreDtos 学生成绩
     * @return List<StudentEvaluationResult>
     */
    private List<StudentEvaluationResult> getFilterResults2(String evaluationResult, List<StudentScoreDto> studentScoreDtos) {
        List<StudentEvaluationResult> results = new ArrayList<>(studentScoreDtos.size());
        for (StudentScoreDto studentScoreDto : studentScoreDtos) {
            List<CourseEvaluation> courseEvaluations = studentScoreDto.getStuCourse();
            boolean isMet = this.meetScoreRequirements(courseEvaluations);
            if (isMet) {
                StudentEvaluationResult result = new StudentEvaluationResult();
                BeanUtils.copyProperties(studentScoreDto, result);
                result.setEnglishScore(String.valueOf(studentScoreDto.getEnglishScore()));
                result.setEvaluationResult(evaluationResult);
                results.add(result);
            }
        }
        return results;
    }

    /**
     * 发展性素质分排序
     *
     * @param results 评选结果
     * @return
     */
    private List<StudentEvaluationResult> getSortResults(List<StudentEvaluationResult> results) {
        Collections.sort(results, new Comparator<StudentEvaluationResult>() {
            @Override
            public int compare(StudentEvaluationResult o1, StudentEvaluationResult o2) {
                Double extraScore1 = o1.getExtraScore();
                Double extraScore2 = o2.getExtraScore();
                return extraScore1 > extraScore2 ? -1 : extraScore1 < extraScore2 ? 1 : 0;
            }
        });
        return results;
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
     * 各科成绩满足要求
     *
     * @param courseEvaluations
     * @return
     */
    private boolean meetScoreRequirements(List<CourseEvaluation> courseEvaluations) {
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
