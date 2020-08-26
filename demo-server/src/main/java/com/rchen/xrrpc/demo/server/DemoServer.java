package com.rchen.xrrpc.demo.server;

import com.rchen.xrrpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Slf4j
public class DemoServer {
    public static void main(String[] args) {
        ApplicationContext ctx =  new ClassPathXmlApplicationContext("spring-server.xml");
        RpcServer rpcServer = ctx.getBean(RpcServer.class);
        rpcServer.startService();
    }
}
