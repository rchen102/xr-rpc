package com.rchen.xrrpc.client;

import com.rchen.xrrpc.client.netty.NettyClient;

import java.util.Hashtable;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class ClientManager {

    private volatile static ClientManager clientManager;

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
            if (client == null) {
                client = new NettyClient(ip, port);
                clientCache.put(serviceAddress, client);
            }
        }
        //TODO 判断该 client 是否可用，可能已经出现错误，pdf P135
        return client;
    }
}
