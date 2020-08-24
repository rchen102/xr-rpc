package com.rchen.xrrpc.demo.server.service.impl;

import com.rchen.xrrpc.annotation.RpcService;
import com.rchen.xrrpc.demo.api.service.HelloService;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@RpcService(value = HelloService.class, version = "1.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Hello, World!";
    }

    @Override
    public String sayHello(String msg) {
        return msg;
    }
}
