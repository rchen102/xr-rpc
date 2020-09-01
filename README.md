# XR-RPC
A Light-weight distributed RPC Framework.

## Tech Stack
- Java: JDK8
- Spring: service scan, configure and manage Java objects
- Netty: network framework for development of high performance servers and clients
- Zookeeper: service registry and service discovery
- SLF4J + Logback: logger
- JUnit: unit test

## How to Run

### Define Service (Interface)

> Please refer to demo-api module

```java
package com.rchen.xrrpc.demo.api.service;

public interface HelloService {
    String sayHello();
}
```

### Deploy Service

> Please refer to demo-server module

1. Add Maven Dependencies

    ```xml
    <!-- RPC Core -->
    <dependency>
        <groupId>com.rchen</groupId>
        <artifactId>xr-rpc-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!-- RPC Sample API -->
    <dependency>
        <groupId>com.rchen</groupId>
        <artifactId>demo-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
2. Implement RPC Interface
    
    ```java
    package com.rchen.xrrpc.demo.server.service.impl;
    
    import com.rchen.xrrpc.annotation.RpcService;
    import com.rchen.xrrpc.demo.api.service.HelloService;
    
    @RpcService(value = HelloService.class, version = "1.0")
    public class HelloServiceImpl implements HelloService {
        @Override
        public String sayHello() {
            return "Hello, World!";
        }
    }
    ```

3. Configure Server
    
    *spring-server.xml*
    ```xml
    <context:property-placeholder location="classpath:rpc.properties"/>
    
    <context:component-scan base-package="com.rchen.xrrpc.demo.server" />
    
    <!-- service registry  -->
    <!-- Zookeeper Znode Path: registry/serviceName/serviceAddress -->
    <bean id="serviceRegistry" class="com.rchen.xrrpc.registry.impl.ZookeeperServiceRegistry" >
       <constructor-arg name="zkAddress" value="${rpc.registry_address}" />
    </bean>
    
    <!-- RPC Server  -->
    <bean id="rpcServer" class="com.rchen.xrrpc.server.RpcServer">
       <constructor-arg name="serviceAddress" value="${rpc.service_address}"/>
       <constructor-arg name="serviceRegistry" ref="serviceRegistry" />
    </bean>
    
    <!-- Server Config  -->
    <bean id="serverConfig" class="com.rchen.xrrpc.config.ServerConfig" >
       <property name="verifyCode" value="${rpc.verification_code}" />
    </bean>
    ```
    
    *rpc.properties*
    ```properties
    ## code to verify reliable client
    rpc.verification_code=admin123
    
    ## rpc service listen at the address [ip:port]
    rpc.service_address=192.168.xxx.xxx:8000
    
    ## address of service registry [ip:port]
    rpc.registry_address=192.168.xxx.xxx:2181
    ```

4. Start RPC Server

    ```java
    package com.rchen.xrrpc.demo.server;
    
    import com.rchen.xrrpc.server.RpcServer;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public class DemoServer {
        public static void main(String[] args) {
            ApplicationContext ctx =  new ClassPathXmlApplicationContext("spring-server.xml");
            RpcServer rpcServer = ctx.getBean(RpcServer.class);
            rpcServer.startService();
        }
    }
    ```

### Request Service

> Please refer to demo-client module 

1. Add Maven Dependencies

    ```xml
    <!-- RPC Core -->
    <dependency>
        <groupId>com.rchen</groupId>
        <artifactId>xr-rpc-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!-- RPC Sample API -->
    <dependency>
        <groupId>com.rchen</groupId>
        <artifactId>demo-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
    
2. Configure Client

    *spring-client.xml*
    ```xml
    <context:property-placeholder location="classpath:rpc.properties"/>
    
   <!-- Service Discovery -->
    <bean id="serviceDiscovery" class="com.rchen.xrrpc.registry.impl.ZookeeperServiceDiscovery">
        <constructor-arg name="zkAddress" value="${rpc.discovery_address}"/>
    </bean>
    
   <!-- RPC Client -->
    <bean id="rpcClient" class="com.rchen.xrrpc.client.RpcClient">
        <constructor-arg name="serviceDiscovery" ref="serviceDiscovery" />
    </bean>
    
   <!--Client Config -->
    <bean id="clientConfig" class="com.rchen.xrrpc.config.ClientConfig">
        <property name="verifyCode" value="${rpc.verification_code}"/>
        <property name="rpcMaxTimeout" value="${rpc.max_timeout}" />
    </bean>
    ```

    *rpc.properties*
    ```properties
    ## code for verification
    rpc.verification_code=admin123
    
    ## RPC request max timeout
    rpc.max_timeout=5000 
    
    ## address for service discovery [ip:port]
    rpc.discovery_address=192.168.xxx.xxx:2181
    ```
   
3. Call RPC Service

    ```java
    package com.rchen.demo.client;
    
    import com.rchen.xrrpc.client.RpcClient;
    import com.rchen.xrrpc.client.proxy.AsyncRpcCallback;
    import com.rchen.xrrpc.client.proxy.AsyncRpcProxy;
    import com.rchen.xrrpc.demo.api.service.HelloService;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public class DemoClient {
        public static void main(String[] args) {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
            RpcClient rpcClient = context.getBean(RpcClient.class);
    
            try {
                // synchronous rpc call
                HelloService helloService = rpcClient.createProxy(HelloService.class, "1.0");
                System.out.println("结果： " + helloService.sayHello());
    
                // asynchronous rpc call
                AsyncRpcProxy asyncRpcProxy = rpcClient.createAsyncProxy(HelloService.class, "1.0");
                asyncRpcProxy.call("sayHello", new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        System.out.println("回调结果（成功）：" + result);
                    }
    
                    @Override
                    public void fail(Exception e) {
                        System.out.println("回调结果（失败）：" + e.getMessage());
                    }
                });
            } finally {
                rpcClient.close();
            }
        }
    }
    ```
