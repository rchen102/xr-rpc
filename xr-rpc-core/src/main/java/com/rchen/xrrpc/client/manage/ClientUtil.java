package com.rchen.xrrpc.client.manage;

import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务连接的工具类
 *
 * @Author : crz
 * @Date: 2020/8/25
 */
@Slf4j
public class ClientUtil {
    public static TransportClient getAvailableClient(ServiceDiscovery discovery, String serviceName) {
        // 1. 服务发现，获取已注册的服务地址 TODO 后续可以增加负载均衡
        TransportClient client = null;
        List<String> addressList = discovery.discover(serviceName);
        if (addressList.size() == 0) {
            log.error("未发现服务 [{}] 的注册地址，请稍后再试！", serviceName);
            return null;
        }

        // 2. 尝试建立服务连接
        for (String serviceAddress : addressList) {
            client = ClientManager.getInstance().getClient(serviceAddress);
            if (client != null) break;
        }
        return client;
    }
}
