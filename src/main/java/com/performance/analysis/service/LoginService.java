package com.performance.analysis.service;

import com.performance.analysis.dao.UserDao;
import com.performance.analysis.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by huangds on 2017/10/28.
 */
@Service
public class LoginService {

    @Resource
    private UserDao userdao;

    public boolean verifyLogin(User user){

        List<User> users=userdao.getUser(user.getUsername(),user.getPassword());
        if(users.size()>0){

            return true;
        }
        return false;
    }



}
