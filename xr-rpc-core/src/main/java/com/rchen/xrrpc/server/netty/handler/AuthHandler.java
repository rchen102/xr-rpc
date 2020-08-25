package com.rchen.xrrpc.server.netty.handler;

import com.rchen.xrrpc.server.netty.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 身份校验 Handler
 *
 * @Author : crz
 * @Date: 2020/8/24
 */
@Slf4j
public class AuthHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!LoginUtil.hasLogin(ctx.channel())) {
            log.debug("请求通道未验证");
            /**
             * 未进行身份校验，直接关闭连接
             */
            ByteBuf buffer = ctx.alloc().buffer();
            byte[] bytes = "身份校验失败，连接即将关闭...".getBytes(Charset.forName("utf-8"));
            buffer.writeBytes(bytes);
            ctx.channel().writeAndFlush(buffer)
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            log.debug("请求通道已验证");
            /**
             * 身份校验已经通过，即可移除，无需每次都校验
             */
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (LoginUtil.hasLogin(ctx.channel())) {
            log.info("当前连接验证完毕，无需再次验证, AuthHandler 被移除");
        } else {
            log.error("当前通道未经过验证，关闭通道");
            ctx.channel().close();
        }
    }
}
