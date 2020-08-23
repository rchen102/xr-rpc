package com.rchen.xrrpc.client;

import com.rchen.xrrpc.protocol.request.RpcRequest;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class TestThread implements Runnable {

    private Channel channel;

    public TestThread(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("输入消息发送至服务端: ");
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();

            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId("00767193")
                    .serviceName("testServiceName")
                    .methodName("testMethodName")
                    .paramsType(null)
                    .params(null)
                    .build();
            channel.writeAndFlush(rpcRequest);
        }
    }
}
