package com.rchen.xrrpc.registry.impl;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
public interface ZookeeperConfig {
    int ZK_SESSION_TIMEOUT = 5000;
    int ZK_CONNECTION_TIMEOUT = 1000;

    String ZK_REGISTRY_PATH = "/registry";
}
