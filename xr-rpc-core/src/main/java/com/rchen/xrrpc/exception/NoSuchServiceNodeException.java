package com.rchen.xrrpc.exception;

/**
 * @Author : crz
 * @Date: 2020/8/25
 */
public class NoSuchServiceNodeException extends RuntimeException {
    public NoSuchServiceNodeException(String message) {
        super(message);
    }
}
