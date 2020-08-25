package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.demo.api.service.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class DemoClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
        RpcClient rpcClient = context.getBean(RpcClient.class);

        try {
            HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
            System.out.println("结果： " + helloService.sayHello());

            HelloService helloService2 = rpcClient.createProxy(HelloService.class, "2.0");
            System.out.println("结果： " + helloService2.sayHello());
        } finally {
            rpcClient.close();
        }



//
//        StudentService studentService = rpcClient.createProxy(StudentService.class, "1.0");
//        System.out.println("结果： " + studentService.findById("0076"));
//        System.out.println("结果： " + studentService.getCollege(new Student("0076", "rchen")));
    }
}
