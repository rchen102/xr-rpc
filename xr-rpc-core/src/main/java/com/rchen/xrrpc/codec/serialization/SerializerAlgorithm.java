package com.rchen.xrrpc.codec.serialization;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface SerializerAlgorithm {
    /**
     * JSON 序列化算法
     */
    byte JSON = 1;

    /**
     * Protobuf 序列化算法
     */
    byte PROTOBUF = 2;
}
