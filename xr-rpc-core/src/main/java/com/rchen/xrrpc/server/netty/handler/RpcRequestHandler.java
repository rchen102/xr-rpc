package com.rchen.xrrpc.server.netty.handler;

import com.rchen.xrrpc.exception.NoSuchServiceException;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.protocol.response.RpcResponse;
import com.rchen.xrrpc.util.ReflectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String, Object> serviceBeanMap;

    public RpcRequestHandler(Map<String, Object> serviceBeanMap) {
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        log.debug("收到客户端消息：{}", rpcRequest.toString());
        RpcResponse rpcResponse;
        // 1. 解析 RpcRequest
        String requestId = rpcRequest.getRequestId();
        String serviceName = rpcRequest.getServiceName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] paramsType = rpcRequest.getParamsType();
        Object[] params = rpcRequest.getParams();

        // 2. 构造 RpcResponse
        Object serviceBean = serviceBeanMap.get(serviceName);
        if (serviceBean == null) {
            log.error("不能识别的 RPC 服务：{}", serviceName);
            rpcResponse = RpcResponse.builder()
                    .requestId(requestId)
                    .exception(new NoSuchServiceException(serviceName + " 服务不存在"))
                    .result(null)
                    .build();
        } else {
            try {
                Object result = ReflectionUtil.invokeMethod(
                        serviceBean, methodName, paramsType, params);
                rpcResponse = RpcResponse.builder()
                        .requestId(requestId)
                        .exception(null)
                        .result(result)
                        .build();
            } catch (NoSuchMethodException ex) {
                log.error("RPC 服务 {} 不存在方法 {}", serviceName, methodName);
                rpcResponse = RpcResponse.builder()
                        .requestId(requestId)
                        .exception(ex)
                        .result(null)
                        .build();
            }
        }
        ctx.channel().writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("连接出现错误：" + ctx.channel().remoteAddress() + " | " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
