package com.performance.analysis.service.impl;

import com.performance.analysis.dao.StudentEvaluationDao;
import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.exception.DataReadInException;
import com.performance.analysis.pojo.StudentEvaluationResult;
import com.performance.analysis.service.DataAnalysisService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Tangwei
 * @Date: 2018/5/31 下午2:14
 * <p>
 * TripleA三好学生评比分析结果
 */
@Service
public class BuaTripleAResultService implements DataAnalysisService {
    private final static String TRIPLEA = "A";
    private final static Double TRIPLEA_PHYSICAL_SCORE = 80d;
    private final static Double TRIPLEA_MORAL_SCORE = 85d;
    private final static Double TRIPLEA_MAJOR_SCORE = 85d;
    private final static String TRIPLEA_ENGLISH_SCORE = "CET-4";

    @Autowired
    private StudentEvaluationDao studentEvaluationDao;

    @Override
    public List<T> analysis(String... args) {
        List<StudentEvaluationDto> studentEvaluationDtos = studentEvaluationDao.findStudentEvaluations(args[0]);

        return null;
    }

    /**
     * 获取符合TripleA条件数据
     *
     * @param studentEvaluationDtos
     * @return
     */
    private List<StudentEvaluationResult> getTripleAData(List<StudentEvaluationDto> studentEvaluationDtos) throws DataReadInException {
        List<StudentEvaluationResult> studentEvaluationResults = new ArrayList<>();
        StudentEvaluationResult studentEvaluationResult;
        for (StudentEvaluationDto dto : studentEvaluationDtos) {
            if (dto.getPhysicalScore() != null && dto.getPhysicalScore() <= TRIPLEA_PHYSICAL_SCORE) {
                continue;
            }
            if (dto.getPhysicalScore() != null && dto.getMoralScore() <= TRIPLEA_MORAL_SCORE) {
                continue;
            }
            if (dto.getPhysicalScore() != null && dto.getMajorScore() <= TRIPLEA_MAJOR_SCORE) {
                continue;
            }
            Integer grade = BuaAnalyticalRule.getGrade(dto.getStuNo());
            if (grade > 1) {
                if (!dto.getEnglishScore().equals(TRIPLEA_ENGLISH_SCORE)) {
                    continue;
                }
            } else {
                //TODO中位数计算
            }
            studentEvaluationResult = new StudentEvaluationResult();
            studentEvaluationResult.setEnglishScore(dto.getEnglishScore());
            studentEvaluationResult.setEvaluationResult(TRIPLEA);
            studentEvaluationResult.setMajorScore(dto.getMajorScore());
            studentEvaluationResult.setMoralScore(dto.getMoralScore());
            studentEvaluationResult.setPhysicalScore(dto.getPhysicalScore());
            studentEvaluationResult.setStuName(dto.getStuName());
            studentEvaluationResult.setStuNo(dto.getStuNo());
            studentEvaluationResults.add(studentEvaluationResult);
        }
        return studentEvaluationResults;
    }

}
