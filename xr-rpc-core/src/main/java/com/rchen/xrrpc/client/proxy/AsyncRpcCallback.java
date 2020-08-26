package com.rchen.xrrpc.client.proxy;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public interface AsyncRpcCallback {
    void success(Object result);

    void fail(Exception e);
}
