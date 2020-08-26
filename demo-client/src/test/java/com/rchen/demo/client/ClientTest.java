package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.demo.api.service.HelloService;
import com.rchen.xrrpc.demo.api.service.StudentService;
import com.rchen.xrrpc.exception.RpcFailureException;
import com.rchen.xrrpc.exception.RpcTimeoutException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试同步 RPC 调用
 *
 * @Author : crz
 * @Date: 2020/8/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-client.xml")
public class ClientTest {

    @Autowired
    private RpcClient rpcClient;

    /**
     * 测试 RPC 调用
     */
    @Test
    public void testRpc() {
        try {
            HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
            System.out.println("执行结果: " + helloService.sayHello());

            StudentService stuService = rpcClient.createProxy(StudentService.class, "1.0");
            System.out.println("执行结果: " + stuService.findById("0076"));
        } finally {
            // 关闭客户端
            rpcClient.close();
        }
    }

    /**
     * 测试 RPC 调用超时
     */
    @Test
    public void testRpcTimeout() {
        try {
            HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
            System.out.println("执行结果: " + helloService.sayHelloLater());
        } catch (RpcTimeoutException timeout) {
            timeout.printStackTrace();
        }
        finally {
            // 关闭客户端
            rpcClient.close();
        }
    }

    /**
     * 测试 RPC执行失败，出现 Exception
     */
    @Test
    public void testRpcWithException() {
        try {
            HelloService helloService = rpcClient.createProxy(HelloService.class, "2.0");
            System.out.println("执行结果: " + helloService.sayHello());
        } catch (RpcFailureException rpc) {
            rpc.printStackTrace();
        } finally {
            rpcClient.close();
        }
    }
}
