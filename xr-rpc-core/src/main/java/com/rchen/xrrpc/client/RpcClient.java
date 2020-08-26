package com.rchen.xrrpc.client;

import com.rchen.xrrpc.client.manage.ClientManager;
import com.rchen.xrrpc.client.proxy.AsyncRpcProxy;
import com.rchen.xrrpc.client.proxy.RpcProxy;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
@Data
@AllArgsConstructor
public class RpcClient {

    private ServiceDiscovery serviceDiscovery;

    /**
     * 创建用于同步调用的 RPC 代理对象
     * @param serviceAPI
     * @param <T>
     * @return
     */
    public <T> T createProxy(Class<T> serviceAPI, String version) {
        return (T) Proxy.newProxyInstance(
                serviceAPI.getClassLoader(),
                new Class<?>[]{serviceAPI},
                new RpcProxy(serviceDiscovery, version)
        );
    }

    /**
     * 创建用于异步调用的 RPC 代理对象
     * @param serviceAPI
     * @param <T>
     * @return
     */
    public <T> AsyncRpcProxy createAsyncProxy(Class<T> serviceAPI, String version) {
        return new AsyncRpcProxy<T>(serviceDiscovery, serviceAPI, version);
    }

    public void close() {
        log.info("RPC 客户端准备关闭...");
        serviceDiscovery.close();
        ClientManager.getInstance().close();
    }
}
