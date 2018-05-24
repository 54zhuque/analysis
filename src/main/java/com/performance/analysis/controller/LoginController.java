package com.performance.analysis.controller;

import com.performance.analysis.WebSecurityConfig;
import com.performance.analysis.pojo.User;
import com.performance.analysis.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;

/**
 * Created by huangds on 2017/10/24.
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY)String account, Model model){

        return "index";
    }

    @GetMapping("/test")
    public String test(){

        return "test";
    }

    @GetMapping("/login")
    public String login(){

        return "login";
    }

    @RequestMapping("/loginVerify")
    public String loginVerify(String userName,String password,HttpSession session){

        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);

        boolean verify = loginService.verifyLogin(user);
        if (verify) {
            session.setAttribute(WebSecurityConfig.SESSION_KEY, userName);
            return "index";
        } else {
            return "redirect:/login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/login";
    }
}
