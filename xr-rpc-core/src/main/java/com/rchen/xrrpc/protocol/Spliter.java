package com.rchen.xrrpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Packet 分割，解决拆包和粘包问题
 * 同时对协议进行校验，拒绝非本协议连接
 *
 * @Author : crz
 * @Date: 2020/8/24
 */
@Slf4j
public class Spliter extends LengthFieldBasedFrameDecoder {

    private static final int LENGTH_FIELD_OFFSET = Protocol.LENGTH_FIELD_OFFSET;
    private static final int LENGTH_FIELD_LENGTH = Protocol.LENGTH_FIELD_LENGTH;

    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    /**
     * 屏蔽非本协议的客户端
     * @param ctx
     * @param in
     * @return
     * @throws Exception
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 屏蔽非本协议的客户端
        if (in.getInt(in.readerIndex()) != Protocol.MAGIC_NUMBER) {
            log.error("收到不符合协议数据包，关闭连接...");
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
