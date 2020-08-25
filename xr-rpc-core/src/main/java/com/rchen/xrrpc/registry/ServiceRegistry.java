package com.rchen.xrrpc.registry;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface ServiceRegistry {
    /**
     * 注册服务名称和服务地址
     *
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName, String serviceAddress);
}
