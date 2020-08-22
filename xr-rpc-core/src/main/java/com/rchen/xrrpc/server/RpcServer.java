package com.rchen.xrrpc.server;

import com.rchen.xrrpc.annotation.RpcService;
import com.rchen.xrrpc.server.netty.NettyServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class RpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * 服务地址
     */
    private String serviceAddress;

    private TransportServer transportServer;

    private Map<String, Object> serviceBeanMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描获取所有 @RpcService 标注的服务真实实现类 <BeanName, Bean>
        Map<String, Object> serviceImplMap = ctx.getBeansWithAnnotation(RpcService.class);

        // 创建 serviceBeanMap，<key: serviceName + version，value: serviceBean>
        for (Object serviceBean : serviceImplMap.values()) {
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            String serviceName = rpcService.value().getName() + "-" + rpcService.version();
            this.serviceBeanMap.put(serviceName, serviceBean);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] addressArray = serviceAddress.split(":");
        String ip = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);

        transportServer = new NettyServer(ip, port, this.serviceBeanMap);
    }

    public void startService() {
        transportServer.start();
    }
}
