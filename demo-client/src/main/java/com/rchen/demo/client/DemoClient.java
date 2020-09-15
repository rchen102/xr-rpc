package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.demo.api.service.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class DemoClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
        RpcClient rpcClient = context.getBean(RpcClient.class);

        try {
            // synchronous rpc call
            HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
            System.out.println("结果： " + helloService.sayHello());

            // asynchronous rpc call
//            AsyncRpcProxy asyncRpcProxy = rpcClient.createAsyncProxy(HelloService.class, "1.0");
//            asyncRpcProxy.call("sayHello", new AsyncRpcCallback() {
//                @Override
//                public void success(Object result) {
//                    System.out.println("回调结果（成功）：" + result);
//                }
//
//                @Override
//                public void fail(Exception e) {
//                    System.out.println("回调结果（失败）：" + e.getMessage());
//                }
//            });
        } finally {
            rpcClient.close();
        }
    }
}
