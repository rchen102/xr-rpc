package com.rchen.xrrpc.server.netty.attr;

import io.netty.util.AttributeKey;

/**
 * 声明 AttributeKey
 *
 * @Author : crz
 * @Date: 2020/8/24
 */
public interface Attributes {
    /**
     * 用于标记验证是否通过，通过即登录成功
     */
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
