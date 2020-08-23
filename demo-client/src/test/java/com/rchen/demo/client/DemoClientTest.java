package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-client.xml")
public class DemoClientTest {

    @Autowired
    private RpcClient rpcClient;
}
