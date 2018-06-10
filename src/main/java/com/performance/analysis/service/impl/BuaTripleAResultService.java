package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluationEnum;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaDataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午2:14
 * <p>
 * TripleA三好学生评比分析结果
 */
@Service
public class BuaTripleAResultService implements BuaDataAnalysisService {
    private final static Double TRIPLEA_PHYSICAL_SCORE = 80d;
    private final static Double TRIPLEA_MORAL_SCORE = 85d;
    private final static Double TRIPLEA_MAJOR_SCORE = 85d;
    private final static String TRIPLEA_ENGLISH_SCORE = "CET-4";

    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    public List<StudentEvaluationResult> majorGradeAnalysis(String majorGrade) throws DataReadInException {
        List<StudentEvaluationDto> studentEvaluationDtos = studentEvaluationDao.findStudentEvaluations(majorGrade);
        List<StudentEvaluationResult> tripleAStudent = this.getTripleAData(studentEvaluationDtos);
        return tripleAStudent;
    }

    /**
     * 获取符合TripleA条件数据，计算条件：
     * 身体素质分大于80
     * 思想素质分大于85
     * 专业素质分大于85
     * 非一年级学生无需要CET-4，一年级学生英语成绩在专业50%以上
     *
     * @param studentEvaluationDtos
     * @return
     */
    private List<StudentEvaluationResult> getTripleAData(List<StudentEvaluationDto> studentEvaluationDtos) throws DataReadInException {
        if (studentEvaluationDtos == null || studentEvaluationDtos.size() == 0) {
            return null;
        }
        //获取年级
        Integer grade = BuaAnalyticalRule.getGrade(studentEvaluationDtos.get(0).getStuNo());
        List<StudentEvaluationResult> studentEvaluationResults = new ArrayList<>();
        StudentEvaluationResult studentEvaluationResult;
        //英语成绩中位数计算
        Double englishMedianScore = this.getMedianNum(studentEvaluationDtos);
        for (StudentEvaluationDto dto : studentEvaluationDtos) {
            //身体素质不大于80分不符合
            if (dto.getPhysicalScore() != null && dto.getPhysicalScore() <= TRIPLEA_PHYSICAL_SCORE) {
                continue;
            }
            //思想素质不大于85分不符合
            if (dto.getMoralScore() != null && dto.getMoralScore() <= TRIPLEA_MORAL_SCORE) {
                continue;
            }
            //专业素质不大于85分不符合
            if (dto.getMajorScore() != null && dto.getMajorScore() <= TRIPLEA_MAJOR_SCORE) {
                continue;
            }
            //非一年级学生无需要CET-4，一年级学生英语成绩在专业50%以上
            if (grade > 1) {
                if (!dto.getEnglishScore().equals(TRIPLEA_ENGLISH_SCORE)) {
                    continue;
                }
            } else {
                if (Double.valueOf(dto.getEnglishScore()) <= englishMedianScore) {
                    continue;
                }
            }
            studentEvaluationResult = new StudentEvaluationResult();
            studentEvaluationResult.setEnglishScore(dto.getEnglishScore());
            studentEvaluationResult.setEvaluationResult(BuaEvaluationEnum.TRIPLEA.getValue());
            studentEvaluationResult.setMajorScore(dto.getMajorScore());
            studentEvaluationResult.setMoralScore(dto.getMoralScore());
            studentEvaluationResult.setPhysicalScore(dto.getPhysicalScore());
            studentEvaluationResult.setStuName(dto.getStuName());
            studentEvaluationResult.setStuNo(dto.getStuNo());
            studentEvaluationResults.add(studentEvaluationResult);
        }
        return studentEvaluationResults;
    }

    /**
     * 计算中位数
     *
     * @param studentEvaluationDtos
     * @return
     */
    private Double getMedianNum(List<StudentEvaluationDto> studentEvaluationDtos) {
        Double scoreMedianNum;
        if (studentEvaluationDtos == null || studentEvaluationDtos.size() == 0) {
            return null;
        }
        LinkedList<Integer> sortList = new LinkedList<>();
        for (StudentEvaluationDto dto : studentEvaluationDtos) {
            String englishScore = dto.getEnglishScore();
            if (StringUtils.isEmpty(englishScore) || englishScore.equals(TRIPLEA_ENGLISH_SCORE)) {
                continue;
            }
            Integer score = Integer.valueOf(englishScore);
            if (sortList.size() == 0 || score < sortList.getFirst()) {
                sortList.addFirst(score);
            } else {
                for (int i = 0; i < sortList.size(); i++) {
                    //存在比列表中数据小的值，选取合适位置插入
                    if (score < sortList.get(i)) {
                        sortList.add(i, score);
                        break;
                    }
                    if (i == sortList.size() - 1) {
                        sortList.addLast(score);
                    }
                }
            }
        }
        if (sortList.size() == 0) {
            return null;
        }
        //奇数偶数
        boolean even = sortList.size() % 2 == 0;
        if (even) {
            int index = sortList.size() / 2;
            scoreMedianNum = Double.valueOf(sortList.get(index - 1) + sortList.get(index)) / 2;
        } else {
            scoreMedianNum = Double.valueOf(sortList.get((sortList.size() + 1) / 2 - 1));
        }
        return scoreMedianNum;
    }
}
