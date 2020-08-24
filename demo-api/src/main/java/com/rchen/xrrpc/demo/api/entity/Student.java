package com.rchen.xrrpc.demo.api.entity;

import lombok.Data;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Data
public class Student {
    private String id;
    private String name;

    public Student() {
    }

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
