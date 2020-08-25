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
        log.info("收到 RPC 请求[id={}]，开始处理...", rpcRequest.getRequestId());
        log.debug("具体请求: {}", rpcRequest.toString());
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
            } catch (Exception ex) {
                log.error("RPC 请求[id={}] <{} - {}> 执行出现错误", requestId, serviceName, methodName);
                rpcResponse = RpcResponse.builder()
                        .requestId(requestId)
                        .exception(ex)
                        .result(null)
                        .build();
            }
        }
        log.info("RPC 请求[id={}]处理完毕，发送执行结果", requestId);
        ctx.channel().writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("连接出现错误：" + ctx.channel().remoteAddress() + " | " + cause.getMessage());
        ctx.close();
    }
}
