package com.rchen.xrrpc.demo.server;

import com.rchen.xrrpc.server.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class DemoServer {
    public static void main(String[] args) {
        ApplicationContext ctx =  new ClassPathXmlApplicationContext("spring-server.xml");
        RpcServer rpcServer = ctx.getBean(RpcServer.class);
        rpcServer.startService();
    }
}
