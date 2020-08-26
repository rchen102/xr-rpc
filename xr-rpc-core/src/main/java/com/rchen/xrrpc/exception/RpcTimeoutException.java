package com.rchen.xrrpc.exception;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
public class RpcTimeoutException extends RuntimeException {
    public RpcTimeoutException(String message) {
        super(message);
    }
}
