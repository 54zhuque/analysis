package com.performance.analysis.service.impl;

import com.performance.analysis.common.BuaEvaluationEnum;
import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.exception.DataAnalysisException;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.BuaDataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private final static String CET4 = "CET4";

    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = DataAnalysisException.class)
    public List<StudentEvaluationResult> majorGradeAnalysis(Integer grade, String major) throws DataAnalysisException {
        List<StudentEvaluationResult> tripleAStudents = studentEvaluationDao.
                findStudentEvaluationByMajorGrade(BuaEvaluationEnum.TRIPLEA.getValue(), grade, major);
        if (tripleAStudents != null && tripleAStudents.size() > 0) {
            return tripleAStudents;
        }
        List<StudentEvaluationDto> studentEvaluationDtos = studentEvaluationDao.findStudentEvaluations(grade, major);
        try {
            tripleAStudents = this.getTripleAData(studentEvaluationDtos);
        } catch (Exception e) {
            throw new DataAnalysisException(e.getMessage());
        }
        if (tripleAStudents != null) {
            for (StudentEvaluationResult tripleAStudent : tripleAStudents) {
                //保存评优结果
                studentEvaluationDao.addStudentEvaluationResult(tripleAStudent);
            }
            //重新查询排序后的结果
            tripleAStudents = studentEvaluationDao.
                    findStudentEvaluationByMajorGrade(BuaEvaluationEnum.TRIPLEA.getValue(), grade, major);
        }
        return tripleAStudents;
    }

    /**
     * 获取符合三好学生条件数据，计算条件：
     * 身体素质分大于80
     * 思想素质分大于85
     * 专业素质分大于85
     * 一年级学生英语成绩在专业50%以上,二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
     *
     * @param studentEvaluationDtos
     * @return
     */
    private List<StudentEvaluationResult> getTripleAData(List<StudentEvaluationDto> studentEvaluationDtos) throws DataReadInException {
        if (studentEvaluationDtos == null || studentEvaluationDtos.size() == 0) {
            return null;
        }
        List<StudentEvaluationResult> studentEvaluationResults = new ArrayList<>();
        StudentEvaluationResult studentEvaluationResult;
        //获取通过CET-4列表
        List<String> cet4List = studentEvaluationDao.getEnglishCET4List();
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
            //一年级学生英语成绩在专业50%以上,二年级学生需要英语成绩在专业50%以上或者CET-4，其他需要CET-4
            if (dto.getStuGrade() == 1) {
                if (Double.valueOf(dto.getEnglishScore()) < englishMedianScore) {
                    continue;
                }
            } else if (dto.getStuGrade() == 2) {
                if (Double.valueOf(dto.getEnglishScore()) < englishMedianScore) {
                    if (!cet4List.contains(dto.getStuNo())) {
                        continue;
                    }
                }
            } else {
                if (!cet4List.contains(dto.getStuNo())) {
                    continue;
                }
            }
            studentEvaluationResult = new StudentEvaluationResult();
            String englishScore = cet4List.contains(dto.getStuNo()) ? CET4 : dto.getEnglishScore();
            studentEvaluationResult.setEnglishScore(englishScore);
            studentEvaluationResult.setEvaluationResult(BuaEvaluationEnum.TRIPLEA.getValue());
            studentEvaluationResult.setMajorScore(dto.getMajorScore());
            studentEvaluationResult.setMoralScore(dto.getMoralScore());
            studentEvaluationResult.setPhysicalScore(dto.getPhysicalScore());
            studentEvaluationResult.setStuName(dto.getStuName());
            studentEvaluationResult.setStuGrade(dto.getStuGrade());
            studentEvaluationResult.setStuNo(dto.getStuNo());
            studentEvaluationResult.setFixScore(BuaAnalyticalRule.getWeightedScore(BuaAnalyticalRule.getTrableAEvaluationWeights(),
                    dto.getPhysicalScore(), dto.getMajorScore(), dto.getMoralScore()));
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
            if (StringUtils.isEmpty(englishScore)) {
                continue;
            }
            Integer score = Integer.valueOf(englishScore);
            if (sortList.size() == 0 || score < sortList.getFirst()) {
                sortList.addFirst(score);
            } else {
                int len = sortList.size();
                for (int i = 0; i < len; i++) {
                    //存在比列表中数据小的值，选取合适位置插入
                    if (score < sortList.get(i)) {
                        sortList.add(i, score);
                        break;
                    }
                    if (i == len - 1) {
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
