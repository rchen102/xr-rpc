package com.rchen.xrrpc.client.netty.handler;

import com.rchen.xrrpc.client.RpcFuture;
import com.rchen.xrrpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Map<String, RpcFuture> pending;

    public RpcResponseHandler(Map<String, RpcFuture> pending) {
        this.pending = pending;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        RpcFuture rpcFuture = pending.get(requestId);
        if (rpcFuture != null) {
            pending.remove(rpcFuture);
            rpcFuture.done(rpcResponse);
        } else {
            log.error("收到 RPC 请求回复[id={}]，但是 Future 不存在", requestId);
        }
    }
}
