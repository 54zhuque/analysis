package com.performance.analysis.controller;

import com.performance.analysis.dto.StudentEvaluationDto;
import com.performance.analysis.pojo.EnglishEvaluation;
import com.performance.analysis.pojo.MajorEvaluation;
import com.performance.analysis.pojo.MoralEvaluation;
import com.performance.analysis.pojo.PhysicalEvaluation;
import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.StudentService;
import com.performance.analysis.service.VariousGradeService;
import java.util.List;

import com.performance.analysis.service.impl.BuaTripleAResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    private StudentService studentService;
    @Autowired
    private VariousGradeService variousGradeService;

    @GetMapping("/main")
    public ModelAndView showMain() {
        ModelAndView view = new ModelAndView();

        List<StudentEvaluationDto> ses=studentService.studentsResultOverview();
        view.addObject("students", ses);

        view.setViewName("index/main");
        return view;
    }

    @GetMapping("/import/{type}")
    public ModelAndView fileImport(@PathVariable String type) {
        ModelAndView view = new ModelAndView();
        setUploadViewData(view, type);
        view.setViewName("upload/" + type);
        return view;
    }

    /**
     * 上传文件页面数据封装
     * @param view
     * @param type
     */
    private void setUploadViewData(ModelAndView view, String type){
        if(ENGLISH.equals(type)){
            List<EnglishEvaluation> english = variousGradeService.listEnglishEvaluation();
            view.addObject("englishList", english);
        }else if (MAJOY.equals(type)){
            List<MajorEvaluation> majoy = variousGradeService.listMajorEvaluation();
            view.addObject("majoyList", majoy);
        }else if (MORAL.equals(type)){
            List<MoralEvaluation> moral = variousGradeService.listMoralEvaluation();
            view.addObject("moralList", moral);
        }else if (PHYSICAL.equals(type)){
            List<PhysicalEvaluation> physical = variousGradeService.listPhysicalEvaluation();
            view.addObject("physicalList", physical);
        }
    }

}
