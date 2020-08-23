package com.rchen.xrrpc.protocol.codec.serialization.impl.pojo;

import lombok.Data;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Data
public class Request {
    private String requestId;

    private Class<?>[] paramsTypes;

    private Object[] params;
}
