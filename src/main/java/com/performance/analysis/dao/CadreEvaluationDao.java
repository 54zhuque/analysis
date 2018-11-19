package com.performance.analysis.dao;

import com.performance.analysis.pojo.ClassCadre;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Author: 段豆豆
 * @Date: 2018/11/17
 * 额外加分数据访问
 */
@Mapper
@Repository
public interface CadreEvaluationDao {

    /**
     * 添加班级干部
     * @param classCadre
     */
    @Insert("insert or replace into class_cadre(stu_no, stu_name, desc) " +
            "values(#{stuNo}, #{stuName}, #{desc})")
    void addCadreEvaluation(ClassCadre classCadre);

    /**
     * 查询所有的英语成绩
     *
     * @return List<ClassCadre>
     */
    @Select("select * from class_cadre")
    List<ClassCadre> listCadreEvaluation();
}
