package com.rchen.xrrpc.exception;

/**
 * @Author : crz
 * @Date: 2020/8/24
 */
public class RpcFailureException extends RuntimeException {
    public RpcFailureException(Throwable cause) {
        super(cause);
    }
}
