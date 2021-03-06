package com.rchen.xrrpc.demo.server.service.impl;

import com.rchen.xrrpc.annotation.RpcService;
import com.rchen.xrrpc.demo.api.service.HelloService;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@RpcService(value = HelloService.class, version = "2.0")
public class NewHelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        int error = 1/0;
        return "Hi!";
    }

    @Override
    public String sayHelloLater() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Wait!";
    }

    @Override
    public String echo(String msg) {
        return "New: " + msg;
    }
}