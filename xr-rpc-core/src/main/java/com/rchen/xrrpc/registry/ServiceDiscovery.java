package com.rchen.xrrpc.registry;

import java.util.List;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称，获取可用的服务地址
     * @param serviceName 服务名（interfaceName + version）
     * @return 服务地址
     */
    List<String> discover(String serviceName);

    void close();
}
