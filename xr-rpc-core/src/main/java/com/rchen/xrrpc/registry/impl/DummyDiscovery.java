package com.rchen.xrrpc.registry.impl;

import com.rchen.xrrpc.registry.ServiceDiscovery;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@AllArgsConstructor
@Data
public class DummyDiscovery implements ServiceDiscovery {

    private String zkAddress;

    @Override
    public String discover(String serviceName) {
        return "127.0.0.1:8000";
    }
}
