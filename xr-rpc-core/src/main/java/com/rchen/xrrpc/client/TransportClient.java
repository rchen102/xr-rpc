package com.rchen.xrrpc.client;

import com.rchen.xrrpc.protocol.Packet;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface TransportClient {
    RpcFuture sendRequest(Packet request);
}
