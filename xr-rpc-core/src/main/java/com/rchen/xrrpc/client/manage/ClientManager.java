package com.rchen.xrrpc.client.manage;

import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
public class ClientManager {

    /**
     * volatile 避免指令重排序出现错误
     */
    private volatile static ClientManager clientManager;

    private AtomicInteger activeClients = new AtomicInteger(0);
    private CountDownLatch closeCounter = null;

    /**
     * 连接缓存池，用于复用连接，提高性能
     */
    private final Hashtable<String, TransportClient> clientCache = new Hashtable<>();

    /**
     * 单例模式
     */
    private ClientManager() {
    }

    public static ClientManager getInstance() {
        if (clientManager == null) {
            synchronized (ClientManager.class) {
                if (clientManager == null) {
                    clientManager = new ClientManager();
                }
            }
        }
        return clientManager;
    }


    /**
     * 从连接缓存池中获取一个连接
     * @param serviceAddress (ip:port)
     * @return
     */
    public TransportClient getClient(String serviceAddress) {
        String[] addressArray = serviceAddress.split(":");
        String ip = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        // 查找缓存池
        TransportClient client;
        synchronized (clientCache) {
            client = clientCache.get(serviceAddress);
            // 如果连接还不存在，创建新的连接
            if (client == null) {
                client = new NettyClient(ip, port);
                activeClients.incrementAndGet();
            }
            // 只需要判断连接是否可用（可用 => 一定连接）
            if (client.isAvailable()) {
                clientCache.put(serviceAddress, client);
            } else {
                // 连接不可用，断开连接或者回收连接失败的 Client
                client.close(this);
                client = null;
            }
        }
        return client;
    }

    public boolean hasActiveClient() {
        return activeClients.get() != 0;
    }

    public void doneClose() {
        int remain = activeClients.decrementAndGet();
        if (closeCounter != null && remain == 0) {
            // 所有活跃的 Client 执行关闭操作完毕
            closeCounter.countDown();
        }
        if (remain < 0) {
            log.error("发生未知错误，关闭 Client 数量超过已创建数量！");
        }
    }

    public void close() {
        // 当前如果存在 Active Client，全部执行关闭操作
        if (hasActiveClient()) {
            closeCounter = new CountDownLatch(1);
            for (TransportClient client : clientCache.values()) {
                client.close(this);
            }
            // 等待所有 Client 关闭
            try {
                closeCounter.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientCache.clear();
        }
        log.info("客户端关闭成功!");
    }
}
