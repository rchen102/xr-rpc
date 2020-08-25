package com.rchen.xrrpc.registry.impl;

import com.rchen.xrrpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {

    /**
     * Zookeeper 客户端
     */
    private final ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress,
                ZookeeperConfig.ZK_SESSION_TIMEOUT,
                ZookeeperConfig.ZK_CONNECTION_TIMEOUT);
        log.debug("连接 Zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（永久节点）
        String registryPath = ZookeeperConfig.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.debug("创建 registry 节点: {}", registryPath);
        }
        // 创建 service 节点（永久节点）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.debug("创建 service 节点: {}", servicePath);
        }
        // 创建 serviceAddress 节点（临时节点）
        String addressPath = servicePath + "/address-";
        if (!zkClient.exists(addressPath)) {
            zkClient.createEphemeralSequential(addressPath, serviceAddress);
            log.debug("创建 address 节点: {}", addressPath);
        }
    }
}
