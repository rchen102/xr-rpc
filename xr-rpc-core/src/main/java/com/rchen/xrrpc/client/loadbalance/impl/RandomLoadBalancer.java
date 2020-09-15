package com.rchen.xrrpc.client.loadbalance.impl;

import com.rchen.xrrpc.client.loadbalance.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @Author : crz
 */
// 随机选择策略
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String selectOne(List<String> addressList) {
        return addressList.get(new Random().nextInt(addressList.size()));
    }
}
