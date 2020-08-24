package com.rchen.xrrpc.exception;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class NoSuchServiceException extends RuntimeException{
    public NoSuchServiceException(String message) {
        super(message);
    }
}
