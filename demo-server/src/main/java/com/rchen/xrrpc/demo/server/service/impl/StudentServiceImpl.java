package com.rchen.xrrpc.demo.server.service.impl;

import com.rchen.xrrpc.annotation.RpcService;
import com.rchen.xrrpc.demo.api.entity.Student;
import com.rchen.xrrpc.demo.api.service.StudentService;

import java.util.HashMap;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@RpcService(value = StudentService.class, version = "1.0")
public class StudentServiceImpl implements StudentService {

    /**
     * <key = id, value = college>
     */
    private HashMap<String, String> collegeMap;
    /**
     * <key = id, value = Student>
     */
    private HashMap<String, Student> studentMap;

    public StudentServiceImpl() {
        collegeMap = new HashMap<>();
        collegeMap.put("0076", "Computer Science");

        studentMap = new HashMap<>();
        studentMap.put("0076", new Student("0076", "rchen"));
    }

    @Override
    public Student findById(String id) {
        return studentMap.get(id);
    }

    @Override
    public String getCollege(Student student) {
        return collegeMap.get(student.getId());
    }

}
