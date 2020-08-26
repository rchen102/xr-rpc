package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.manage.ClientUtil;
import com.rchen.xrrpc.config.ClientConfig;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import com.rchen.xrrpc.util.IdUtil;
import com.rchen.xrrpc.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Slf4j
public class AsyncRpcProxy<T> {

    private ServiceDiscovery serviceDiscovery;

    private Class<T> serviceAPI;

    private String serviceVersion;

    /**
     * 用于与服务器建立连接
     */
    private TransportClient transportClient;

    public AsyncRpcProxy(ServiceDiscovery serviceDiscovery, Class<T> serviceAPI, String serviceVersion) {
        this.serviceDiscovery = serviceDiscovery;
        this.serviceAPI = serviceAPI;
        this.serviceVersion = serviceVersion;
    }

    public void call(String method,
                          AsyncRpcCallback callback,
                          Object...params) {
        // 1. 获取可用的服务连接
        String serviceName = serviceAPI.getName() + "-" + serviceVersion;
        log.info("执行 RPC 异步请求，准备获取服务 [{}] 地址...", serviceName);
        transportClient = ClientUtil.getAvailableClient(serviceDiscovery, serviceName);
        // 2. 调用服务
        if (transportClient == null) {
            log.error("无法获得服务 [{}] 的一个可用连接，调用失败!", serviceName);
            return;
        }
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(String.valueOf(IdUtil.nextId()))
                .serviceName(serviceAPI.getName() + "-" + serviceVersion)
                .methodName(method)
                .params(params)
                .build();
        /**
         * 基本类型转换
         */
        Class<?>[] paramsTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramsTypes[i] = ReflectionUtil.getClassType(params[i]);
        }
        rpcRequest.setParamsType(paramsTypes);
        RpcFuture rpcFuture = transportClient.sendAsyncRequest(rpcRequest, callback);
        new Thread(() -> {
            rpcFuture.get(ClientConfig.RPC_MAX_TIMEOUT, TimeUnit.MILLISECONDS);
        }).start();
    }
}
