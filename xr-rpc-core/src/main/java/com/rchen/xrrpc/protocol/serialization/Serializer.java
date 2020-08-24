package com.rchen.xrrpc.protocol.serialization;

import com.rchen.xrrpc.protocol.serialization.impl.ProtobufSerializer;

/**
 * 序列化/反序列接口
 *
 * @Author : crz
 * @Date: 2020/8/22
 */
public interface Serializer {

    /**
     * 默认使用 Protobuf 序列化
     */
    Serializer DEFAULT = new ProtobufSerializer();

    /**
     * 获取序列化算法 id
     * @return
     */
    Byte getSerializerAlgorithm();

    /**
     * 对象序列化为字节流
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 字节流反序列化为对象
     * @param clazz 对象的class对象
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
