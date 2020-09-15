package com.rchen.xrrpc.client.loadbalance;

import java.util.List;

/**
 * @Author : crz
 */
public interface LoadBalancer {
    String selectOne(List<String> addressList);
}
