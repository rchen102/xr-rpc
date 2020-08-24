package com.rchen.demo.client;

import com.rchen.xrrpc.client.RpcClient;
import com.rchen.xrrpc.demo.api.entity.Student;
import com.rchen.xrrpc.demo.api.service.HelloService;
import com.rchen.xrrpc.demo.api.service.StudentService;
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

        HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0", false);
        helloService.sayHello();

        StudentService studentService = rpcClient.createProxy(StudentService.class, "1.0", false);
        studentService.getCollege(new Student("0076", "rchen"));
    }
}
