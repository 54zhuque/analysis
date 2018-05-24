package com.performance.analysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by IntelliJ IDEA.
 * analysis
 * User: Administrator
 * Date: 2018/5/24
 * Time: 13:27
 * @author duandoudou
 */
@Controller
public class MainPageController {

    @GetMapping("/main")
    public ModelAndView showMain(){
        ModelAndView view = new ModelAndView();
        view.addObject("name", "duandoudou");
        view.addObject("age", "27");

        view.setViewName("index/main");
        return view;
    }
}
