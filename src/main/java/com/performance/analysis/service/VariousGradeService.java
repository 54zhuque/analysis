package com.performance.analysis.service;

import com.performance.analysis.pojo.*;

import java.util.List;

/**
 * analysis
 * Date: 2018/6/20
 * Time: 18:53
 *
 * @author duandoudou
 */
public interface VariousGradeService {

    /**
     * 查询所有的英语成绩
     * @return List<EnglishEvaluation>
     */
    List<EnglishEvaluation> listEnglishEvaluation();

    /**
     * 查询所有的专业学科成绩
     * @return List<MajorEvaluation>
     */
    List<MajorEvaluation> listMajorEvaluation();

    /**
     * 查询所有的专业学科成绩
     * @return List<MoralEvaluation>
     */
    List<MoralEvaluation> listMoralEvaluation();

    /**
     * 查询所有的体育成绩
     * @return List<PhysicalEvaluation>
     */
    List<PhysicalEvaluation> listPhysicalEvaluation();

    /**
     * 查询所有的体育成绩
     * @return List<ExtraEvaluation>
     */
    List<ExtraEvaluation> listExtraEvaluation();

    /**
     * 查询所有的体育成绩
     * @return List<ExtraEvaluation>
     */
    List<ClassCadre> listCadreEvaluation();

}
