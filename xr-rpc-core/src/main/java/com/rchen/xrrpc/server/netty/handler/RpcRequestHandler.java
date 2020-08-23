package com.rchen.xrrpc.server.netty.handler;

import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        System.out.println("收到客户端消息： " + rpcRequest.toString());
        RpcResponse rpcResponse = RpcResponse.builder()
                .requestId(rpcRequest.getRequestId())
                .result("SUCCESS")
                .exception(null)
                .build();

        ctx.channel().writeAndFlush(rpcResponse);
    }
}
