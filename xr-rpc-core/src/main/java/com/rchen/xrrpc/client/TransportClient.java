package com.rchen.xrrpc.client;

import com.rchen.xrrpc.client.manage.ClientManager;
import com.rchen.xrrpc.client.proxy.AsyncRpcCallback;
import com.rchen.xrrpc.client.proxy.RpcFuture;
import com.rchen.xrrpc.protocol.request.RpcRequest;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface TransportClient {
    /**
     * 同步 RPC 请求
     * @param request
     * @return
     */
    RpcFuture sendRequest(RpcRequest request);

    /**
     * 异步 RPC 请求
     * @param request
     * @param callback
     * @return
     */
    RpcFuture sendAsyncRequest(RpcRequest request, AsyncRpcCallback callback);

    /**
     * 连接是否可用
     * @return
     */
    boolean isAvailable();

    /**
     * 关闭连接，关闭成功后通知 Manager
     * @param manager
     */
    void close(ClientManager manager);
}
