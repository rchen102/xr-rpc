package com.rchen.xrrpc.client.netty.handler;

import com.rchen.xrrpc.client.netty.NettyClient;
import com.rchen.xrrpc.config.ClientConfig;
import com.rchen.xrrpc.protocol.request.VerifyRequest;
import com.rchen.xrrpc.protocol.response.VerifyResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author : crz
 * @Date: 2020/8/24
 */
@Slf4j
public class VerifyResponseHandler extends SimpleChannelInboundHandler<VerifyResponse> {

    private NettyClient client;

    public VerifyResponseHandler(NettyClient client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 1. 创建 身份验证 请求
        VerifyRequest verifyRequest = VerifyRequest.builder()
                .verifyCode(ClientConfig.VERIFY_CODE)
                .build();
        log.info("发送身份验证信息...");
        log.debug("验证码 [{}]", ClientConfig.VERIFY_CODE);
        ctx.channel().writeAndFlush(verifyRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VerifyResponse verifyResponse) throws Exception {
        if (verifyResponse.isSuccess()) {
            log.info("身份校验通过");
            client.setAvailable(true);
        } else {
            log.error("身份校验失败: {}", verifyResponse.getErrorMsg());
        }
        client.getCountDownLatch().countDown();
    }
}
