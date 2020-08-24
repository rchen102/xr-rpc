package com.rchen.xrrpc.client;

import com.rchen.xrrpc.protocol.request.RpcRequest;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface TransportClient {
    RpcFuture sendRequest(RpcRequest request);
    RpcFuture sendAsyncRequest(RpcRequest request, AsyncRpcCallback callback);
}
