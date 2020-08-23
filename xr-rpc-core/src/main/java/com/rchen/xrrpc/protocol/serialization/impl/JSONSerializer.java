package com.rchen.xrrpc.protocol.serialization.impl;

import com.alibaba.fastjson.JSON;
import com.rchen.xrrpc.protocol.serialization.Serializer;
import com.rchen.xrrpc.protocol.Protocol;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class JSONSerializer implements Serializer {
    @Override
    public Byte getSerializerAlgorithm() {
        return Protocol.SerializerAlgorithm.JSON;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
