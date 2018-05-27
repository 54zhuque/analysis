package com.performance.analysis.service;

import com.performance.analysis.dao.StudentDao;
import com.performance.analysis.pojo.Student;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * analysis
 * User: Administrator
 * Date: 2018/5/27
 * Time: 12:26
 * @author Administrator
 */
@Service
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public List<Student> findAllStudent(){
        return studentDao.findAllStudent();
    }
}
