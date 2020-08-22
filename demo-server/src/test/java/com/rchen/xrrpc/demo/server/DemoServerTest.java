package com.rchen.xrrpc.demo.server;

import com.rchen.xrrpc.server.RpcServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-server.xml")
public class DemoServerTest {

    @Autowired
    RpcServer rpcServer;

    @Test
    public void TestConfig() {
        assertEquals("127.0.0.1:8000", rpcServer.getServiceAddress());
    }

    @Test
    public void TestServiceBeanMap() {
        assertEquals(2, rpcServer.getServiceBeanMap().size());
    }
}
