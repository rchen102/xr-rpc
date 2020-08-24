package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.client.ClientManager;
import com.rchen.xrrpc.client.RpcFuture;
import com.rchen.xrrpc.client.TransportClient;
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
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(String.valueOf(IdUtil.nextId()))
                .serviceName(method.getDeclaringClass().getName() + "-" + serviceVersion)
                .methodName(method.getName())
                .paramsType(method.getParameterTypes())
                .params(args)
                .build();

        String serviceAddress = serviceDiscovery.discover("");
        transportClient = ClientManager.getInstance().getClient(serviceAddress);
        if (transportClient != null) {
            RpcFuture rpcFuture = transportClient.sendRequest(rpcRequest);
            return rpcFuture.get();
        }
        log.error("错误：获取可用的服务端连接失败");
        return null;
    }
}
