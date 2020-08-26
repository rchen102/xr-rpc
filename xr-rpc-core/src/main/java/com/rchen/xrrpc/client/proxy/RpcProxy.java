package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.manage.ClientUtil;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import com.rchen.xrrpc.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
public class RpcProxy implements InvocationHandler {

    private ServiceDiscovery serviceDiscovery;

    private TransportClient transportClient;

    private String serviceVersion;

    public RpcProxy(ServiceDiscovery serviceDiscovery, String version) {
        this.serviceDiscovery = serviceDiscovery;
        this.serviceVersion = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 获取可用的服务连接
        String serviceName = method.getDeclaringClass().getName() + "-" + serviceVersion;
        log.info("执行 RPC 同步请求，准备获取服务 [{}] 地址...", serviceName);
        transportClient = ClientUtil.getAvailableClient(serviceDiscovery, serviceName);
        // 2. 调用服务
        if (transportClient != null) {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId(String.valueOf(IdUtil.nextId()))
                    .serviceName(serviceName)
                    .methodName(method.getName())
                    .paramsType(method.getParameterTypes())
                    .params(args)
                    .build();
            RpcFuture rpcFuture = transportClient.sendRequest(rpcRequest);
            return rpcFuture.get();
        }
        log.error("无法获得服务 [{}] 的一个可用连接，调用失败!", serviceName);
        return null;
    }
}
