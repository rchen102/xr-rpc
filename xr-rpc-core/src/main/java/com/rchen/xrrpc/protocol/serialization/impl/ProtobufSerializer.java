package com.rchen.xrrpc.protocol.serialization.impl;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.rchen.xrrpc.protocol.Protocol;
import com.rchen.xrrpc.protocol.serialization.Serializer;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class ProtobufSerializer implements Serializer {

    /**
     * 缓存创建过的 Schema，优化性能
     * 由于可能存在并发访问 cache，因为使用 ConcurrentHashMap
     */
    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    /**
     * 使用 Objenesis 来实例化对象，它是比 Java 反射更加强大
     */
    private static Objenesis objenesis = new ObjenesisStd(true);

    @Override
    public Byte getSerializerAlgorithm() {
        return Protocol.SerializerAlgorithm.PROTOBUF;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            T message = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }
}
