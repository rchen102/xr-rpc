package com.rchen.xrrpc.registry;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名查找服务地址
     * @param serviceName 服务名（interfaceName + version）
     * @return 服务地址
     */
    String discover(String serviceName);
}
