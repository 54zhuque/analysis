package com.performance.analysis.controller;

import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.pojo.*;
import com.performance.analysis.service.ClearDataService;
import com.performance.analysis.service.StudentService;
import com.performance.analysis.service.VariousGradeService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * analysis
 * User: Administrator
 * Date: 2018/5/24
 * Time: 13:27
 *
 * @author duandoudou
 */
@Controller
public class MainPageController {

    private static final String ENGLISH = "english";
    private static final String MAJOY = "majoy";
    private static final String MORAL = "moral";
    private static final String EXTRA = "extra";
    private static final String PHYSICAL = "physical";
    private static final String CADRE = "cadre";
    private static final String SCHOLARSHIP = "W";

    @Autowired
    private StudentService studentService;
    @Autowired
    private VariousGradeService variousGradeService;
    @Autowired
    private ClearDataService clearDataService;

    /**
     * 返回主页面
     *
     * @return 主页面
     */
    @GetMapping("/main")
    public ModelAndView showMain() {
        ModelAndView view = new ModelAndView();

        List<StudentEvaluationDto> ses = studentService.studentsResultOverview();
        view.addObject("students", ses);

        view.setViewName("index/main");
        return view;
    }

    /**
     * 成绩导入页面
     *
     * @param type 成绩类型
     * @return 成绩导入页面
     */
    @GetMapping("/import/{type}")
    public ModelAndView fileImport(@PathVariable String type) {
        ModelAndView view = new ModelAndView();
        setUploadViewData(view, type);
        view.setViewName("upload/" + type);
        return view;
    }

    @GetMapping("/bua/evaluations/{grade}/{subject}")
    public ModelAndView showEvaluationPage(@PathVariable String grade, @PathVariable String subject) {
        ModelAndView view = new ModelAndView();
        view.addObject("grade", grade);
        view.addObject("subject", subject);
        if(SCHOLARSHIP.equalsIgnoreCase(subject)){
            view.setViewName("index/evaluations_scholarship");
        }else{
            view.setViewName("index/evaluations");
        }
        return view;
    }

    /**
     * 上传文件页面数据封装
     *
     * @param view
     * @param type
     */
    private void setUploadViewData(ModelAndView view, String type) {
        if (ENGLISH.equals(type)) {
            List<EnglishEvaluation> english = variousGradeService.listEnglishEvaluation();
            view.addObject("englishList", complementStudentInfo(english));
        } else if (MAJOY.equals(type)) {
            List<MajorEvaluation> majoy = variousGradeService.listMajorEvaluation();
            view.addObject("majoyList", complementStudentInfo(majoy));
        } else if (MORAL.equals(type)) {
            List<MoralEvaluation> moral = variousGradeService.listMoralEvaluation();
            view.addObject("moralList", complementStudentInfo(moral));
        } else if (PHYSICAL.equals(type)) {
            List<PhysicalEvaluation> physical = variousGradeService.listPhysicalEvaluation();
            view.addObject("physicalList", complementStudentInfo(physical));
        } else if (EXTRA.equals(type)) {
            List<ExtraEvaluation> extra = variousGradeService.listExtraEvaluation();
            view.addObject("extraList", complementStudentInfo(extra));
        } else if (CADRE.equals(type)) {
            List<ClassCadre> cadre = variousGradeService.listCadreEvaluation();
            view.addObject("cadreList", complementStudentInfo(cadre));
        }
    }

    /**
     * 补全学生信息
     * @param dataList
     * @param <T>
     * @return
     */
    private <T> List<T> complementStudentInfo(List<T> dataList) {
        for (T data : dataList) {
            try {
                Class clazz = data.getClass();
                Method getMethod = clazz.getMethod("getStuNo");
                Method setMethod =clazz.getMethod("setStudent", Student.class);
                // 获取学号
                String stuNo = (String)getMethod.invoke(data);
                // 获取学生信息
                Student student = studentService.findStudentByStuNo(stuNo);
                if(student == null){
                    student = new Student();
                }
                // 执行set方法
                setMethod.invoke(data, student);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * 清空分析结果
     *
     */
    @GetMapping("/clear/evaluation")
    public void clearEvaluation(HttpServletResponse resp) {
        try {
            clearDataService.clearStudentEvaluation();
            resp.getWriter().print("success");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    /**
     * 清楚所有数据
     *
     */
    @GetMapping("/clear/all")
    public void clearAllData(HttpServletResponse resp) {
        try {
            clearDataService.clearAllData();
            resp.getWriter().print("success");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
