package com.rchen.xrrpc.protocol.serialization.impl;

import com.rchen.xrrpc.protocol.Protocol;
import com.rchen.xrrpc.protocol.serialization.Serializer;
import com.rchen.xrrpc.protocol.serialization.impl.pojo.Pearson;
import com.rchen.xrrpc.protocol.serialization.impl.pojo.Request;
import com.rchen.xrrpc.protocol.serialization.impl.pojo.Student;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class JSONMySerializerTest {

    static Serializer serializer;
    static Request request;
    static byte[] data;

    @BeforeClass
    public static void beforeClass() throws Exception {

        serializer = new JSONSerializer();
        request = new Request();

        Class<?>[] classes = new Class[2];
        classes[0] = Pearson.class;
        classes[1] = Student.class;
        Object[] params = new Object[2];
        params[0] = new Pearson("rchen");
        params[1] = new Student(22);

        request.setRequestId("123");
        request.setParamsTypes(classes);
        request.setParams(params);
        System.out.println(request);
    }

    @Test
    public void getSerializerAlgorithm() {
        assertEquals(Protocol.SerializerAlgorithm.JSON, serializer.getSerializerAlgorithm());
    }

    @Test
    public void serialize() {
        data = serializer.serialize(request);
        System.out.println(data.length);
        assertNotNull(data);
    }

    @Test
    public void deserialize() {
        Request req = serializer.deserialize(Request.class, data);
        assertEquals(request.getParamsTypes().length, req.getParamsTypes().length);
        assertEquals(request.getParams().length, req.getParams().length);
        System.out.println(req);
    }
}