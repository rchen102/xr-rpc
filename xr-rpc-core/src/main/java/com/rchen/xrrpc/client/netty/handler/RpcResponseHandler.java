package com.rchen.xrrpc.client.netty.handler;

import com.rchen.xrrpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        System.out.println("收到服务端消息： " + rpcResponse.toString());
    }
}
