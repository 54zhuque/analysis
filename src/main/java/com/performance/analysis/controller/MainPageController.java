package com.performance.analysis.controller;

import com.performance.analysis.pojo.Student;
import com.performance.analysis.service.StudentService;
import java.util.List;
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

    @Autowired
    private StudentService studentService;

    @GetMapping("/main")
    public ModelAndView showMain() {
        ModelAndView view = new ModelAndView();

        List<Student> studentList = studentService.findAllStudent();
        view.addObject("students", studentList);

        view.setViewName("index/main");
        return view;
    }

    @GetMapping("/import/{type}")
    public ModelAndView fileImport(@PathVariable String type) {
        ModelAndView view = new ModelAndView();

        List<Student> studentList = studentService.findAllStudent();
        view.addObject("students", studentList);

        view.setViewName("upload/" + type);
        return view;
    }

}
