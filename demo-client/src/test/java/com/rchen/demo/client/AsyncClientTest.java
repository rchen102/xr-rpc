package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.client.proxy.AsyncRpcCallback;
import com.rchen.xrrpc.client.proxy.AsyncRpcProxy;
import com.rchen.xrrpc.demo.api.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试异步 RPC 调用
 *
 * @Author : crz
 * @Date: 2020/8/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-client.xml")
public class AsyncClientTest {

    @Autowired
    private RpcClient rpcClient;

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

        // 等待回调结果
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rpcClient.close();
    }

    /**
     * 测试异步 RPC 调用超时
     */
    @Test
    public void testAsyncRpcTimeout() {
        AsyncRpcProxy asyncRpcProxy = rpcClient.createAsyncProxy(HelloService.class, "1.0");
        asyncRpcProxy.call("sayHelloLater", new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                System.out.println("回调结果（成功）：" + result);
            }

            @Override
            public void fail(Exception e) {
                System.out.println("回调结果（失败）：" + e.getMessage());
            }
        });

        // 等待回调结果
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rpcClient.close();
    }

    /**
     * 测试异步 RPC 调用，执行出现 Exception
     */
    @Test
    public void testAsyncRpcWithException() {
        AsyncRpcProxy asyncRpcProxy = rpcClient.createAsyncProxy(HelloService.class, "2.0");
        asyncRpcProxy.call("sayHello", new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                System.out.println("回调结果（成功）：" + result);
            }

            @Override
            public void fail(Exception e) {
                System.out.println("回调结果（失败）：");
                e.printStackTrace();
            }
        });

        // 等待回调结果
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rpcClient.close();
    }
}
