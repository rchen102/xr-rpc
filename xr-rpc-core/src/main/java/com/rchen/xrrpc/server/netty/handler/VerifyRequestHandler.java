package com.rchen.xrrpc.server.netty.handler;

import com.rchen.xrrpc.config.ServerConfig;
import com.rchen.xrrpc.protocol.request.VerifyRequest;
import com.rchen.xrrpc.protocol.response.VerifyResponse;
import com.rchen.xrrpc.server.netty.util.LoginUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author : crz
 * @Date: 2020/8/24
 */
@Slf4j
public class VerifyRequestHandler extends SimpleChannelInboundHandler<VerifyRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VerifyRequest request) throws Exception {
        log.info("收到客户端验证信息，开始验证...");
        log.debug("收到验证码 [{}]", request.getVerifyCode());
        log.debug("配置验证码 [{}]", ServerConfig.VERIFY_CODE);
        VerifyResponse response;
        if (valid(request)) {
            log.info("客户端验证通过");
            /**
             * 标记当前channel状态为已登陆
             */
            LoginUtil.markAsLogin(ctx.channel());
            response = VerifyResponse.builder()
                    .isSuccess(true)
                    .build();
            ctx.channel().writeAndFlush(response);

        } else {
            log.error("客户端验证失败");
            response = VerifyResponse.builder()
                    .isSuccess(false)
                    .errorMsg("验证码错误，请重试")
                    .build();
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean valid(VerifyRequest request) {
        String clientCode = request.getVerifyCode();
        if (clientCode != null && clientCode.equals(ServerConfig.VERIFY_CODE)) {
            return true;
        }
        return false;
    }
}
