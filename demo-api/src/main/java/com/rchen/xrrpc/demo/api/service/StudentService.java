package com.rchen.xrrpc.demo.api.service;

import com.rchen.xrrpc.demo.api.entity.Student;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public interface StudentService {
    Student findById(String id);
    String getCollege(Student student);
}
