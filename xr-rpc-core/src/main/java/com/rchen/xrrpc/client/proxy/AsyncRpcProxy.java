package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.client.AsyncRpcCallback;
import com.rchen.xrrpc.client.ClientManager;
import com.rchen.xrrpc.client.RpcFuture;
import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import com.rchen.xrrpc.util.IdUtil;
import com.rchen.xrrpc.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

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

    public RpcFuture call(String method,
                          AsyncRpcCallback callback,
                          Object...params) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(String.valueOf(IdUtil.nextId()))
                .serviceName(serviceAPI.getName() + "-" + serviceVersion)
                .methodName(method)
                .params(params)
                .build();
        Class<?>[] paramsTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramsTypes[i] = ReflectionUtil.getClassType(params[i]);
        }
        rpcRequest.setParamsType(paramsTypes);

        String serviceAddress = serviceDiscovery.discover("");
        transportClient = ClientManager.getInstance().getClient(serviceAddress);
        if (transportClient != null) {
            RpcFuture rpcFuture = transportClient.sendAsyncRequest(rpcRequest, callback);
            return rpcFuture;
        }
        log.error("错误：获取可用的服务端连接失败");
        return null;
    }
}
