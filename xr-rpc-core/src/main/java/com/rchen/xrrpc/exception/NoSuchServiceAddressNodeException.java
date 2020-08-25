package com.rchen.xrrpc.exception;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
public class NoSuchServiceAddressNodeException extends RuntimeException {
    public NoSuchServiceAddressNodeException(String message) {
        super(message);
    }
}
