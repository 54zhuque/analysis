package com.performance.analysis.dao;

import com.performance.analysis.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/4/11/011.
 */
@Mapper
public interface UserDao {

    //根据age查找info
    List<User> getUser(@Param("username")String username, @Param("password")String password);
}
