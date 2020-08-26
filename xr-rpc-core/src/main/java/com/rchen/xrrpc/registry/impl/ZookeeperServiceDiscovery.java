package com.rchen.xrrpc.registry.impl;

import com.rchen.xrrpc.exception.NoSuchServiceNodeException;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private final ZkClient zkClient;

    private Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();

    public ZookeeperServiceDiscovery(String zkAddress) {
        log.debug("连接 Zookeeper");
        zkClient = new ZkClient(zkAddress,
                ZookeeperConfig.ZK_SESSION_TIMEOUT,
                ZookeeperConfig.ZK_CONNECTION_TIMEOUT);
    }

    /**
     * 获取服务地址
     * @param serviceName 服务名（interfaceName + version）
     * @return service address
     */
    @Override
    public List<String> discover(String serviceName) {
        List<String> serviceAddressList = new ArrayList<>();
        // 1. 获取 service 节点
        String servicePath = ZookeeperConfig.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            throw new NoSuchServiceNodeException(serviceName + " 节点不存在！");
        }
        log.debug("获取 {} service 节点成功", serviceName);
        // 2. 获取 serviceAddress 节点
        List<String> addressNodeList = zkClient.getChildren(servicePath);
        if (addressNodeList == null || addressNodeList.size() == 0) {
            return serviceAddressList;
        }
        // 3. 读取服务地址
        for (String address : addressNodeList) {
            String addressPath = servicePath + "/" + address;
            String serviceAddress = zkClient.readData(addressPath);
            serviceAddressList.add(serviceAddress);
            log.info("获取服务地址成功：[{}] -> [{}]", serviceName, serviceAddress);
        }
        return serviceAddressList;
    }

    @Override
    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
        log.info("与服务发现中心连接断开!");
    }
}
