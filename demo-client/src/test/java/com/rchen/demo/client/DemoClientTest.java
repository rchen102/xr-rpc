package com.rchen.demo.client;

import com.rchen.xrrpc.client.AsyncRpcCallback;
import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.client.proxy.AsyncRpcProxy;
import com.rchen.xrrpc.demo.api.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-client.xml")
public class DemoClientTest {

    @Autowired
    private RpcClient rpcClient;

    /**
     * 测试同步 RPC 调用
     */
    @Test
    public void testRpc() {
        HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
        helloService.sayHello();

    }

    /**
     * 测试异步 RPC 调用
     */
    @Test
    public void testAsyncRpc() {
        AsyncRpcProxy asyncRpcProxy = rpcClient.createAsyncProxy(HelloService.class, "1.0");
        asyncRpcProxy.call("sayHello", new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                System.out.println("回调结果（成功）：" + result);
            }

            @Override
            public void fail(Exception e) {
                System.out.println("回调结果（失败）：" + e.getMessage());
            }
        });
    }
}
