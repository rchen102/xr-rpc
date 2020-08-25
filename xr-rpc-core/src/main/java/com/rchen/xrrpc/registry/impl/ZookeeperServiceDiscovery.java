package com.rchen.xrrpc.registry.impl;

import com.rchen.xrrpc.exception.NoSuchServiceAddressNodeException;
import com.rchen.xrrpc.exception.NoSuchServiceNodeException;
import com.rchen.xrrpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private String zkAddress;

    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    /**
     * 获取服务地址
     * @param serviceName 服务名（interfaceName + version）
     * @return service address
     */
    @Override
    public String discover(String serviceName) {
        ZkClient zkClient = new ZkClient(zkAddress,
                ZookeeperConfig.ZK_SESSION_TIMEOUT,
                ZookeeperConfig.ZK_CONNECTION_TIMEOUT);
        log.debug("连接 Zookeeper");
        try {
            // 获取 service 节点
            String servicePath = ZookeeperConfig.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new NoSuchServiceNodeException(serviceName + " 节点不存在！");
            }
            log.debug("获取 {} service 节点成功", serviceName);
            // 获取 serviceAddress 节点
            List<String> addressNodeList = zkClient.getChildren(servicePath);
            if (addressNodeList == null || addressNodeList.size() == 0) {
                throw  new NoSuchServiceAddressNodeException(serviceName + " 不存在服务地址节点");
            }
            // 获取一个 serviceAddress 节点
            String address;
            int size = addressNodeList.size();
            if (size == 1) {
                address = addressNodeList.get(0);
            } else {
                //TODO 负载均衡，目前只是随机获取一个节点
                address = addressNodeList.get(ThreadLocalRandom.current().nextInt(size));
            }
            log.debug("获取 {} serviceAddress 节点成功", address);
            // 获得具体得服务地址
            String addressPath = servicePath + "/" + address;
            String serviceAddress = zkClient.readData(addressPath);
            log.info("获取服务地址成功：[{}] -> [{}]", serviceName, serviceAddress);
            return serviceAddress;
        } finally {
            zkClient.close();
        }
    }
}
