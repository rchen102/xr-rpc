package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.demo.api.service.HelloService;
import com.rchen.xrrpc.exception.RpcFailureException;
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
        HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
        System.out.println("执行结果: " + helloService.sayHello());
        // 关闭客户端
        rpcClient.close();
    }

    /**
     * 测试出现错误的 RPC 调用
     */
    @Test
    public void testRpcWithException() {
        try {
            HelloService helloService = rpcClient.createProxy(HelloService.class, "2.0");
            System.out.println("执行结果: " + helloService.sayHello());
        } catch (RpcFailureException rpc) {
            System.out.println("RPC 调用失败");
            rpc.printStackTrace();
        } finally {
            rpcClient.close();
        }
    }
}
