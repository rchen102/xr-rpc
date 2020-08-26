package com.rchen.xrrpc.client.netty;

import com.rchen.xrrpc.client.proxy.AsyncRpcCallback;
import com.rchen.xrrpc.client.manage.ClientManager;
import com.rchen.xrrpc.client.proxy.RpcFuture;
import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.netty.handler.RpcResponseHandler;
import com.rchen.xrrpc.client.netty.handler.VerifyResponseHandler;
import com.rchen.xrrpc.codec.PacketDecoder;
import com.rchen.xrrpc.codec.PacketEncoder;
import com.rchen.xrrpc.protocol.Spliter;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
@Data
public class NettyClient implements TransportClient {
    /**
     * 配置参数
     */
    private static final int MAX_RETRY = 5;
    private String ip;
    private int port;

    /**
     * 连接建立后的 channel
     */
    private Channel channel;
    private NioEventLoopGroup workerGroup;

    /**
     * 尚未收到回复的 Request 的 Future
     * <key = requestId, value = RpcFuture>
     */
    private Map<String, RpcFuture> pending = new ConcurrentHashMap<>();

    /**
     * 用于确保连接在 RPC 调用前先建立
     */
    private CountDownLatch countDownLatch;

    /**
     * 连接是否可用
     * 区分：连接建立 是 连接可用的 前提条件
     */
    private boolean isAvailable;

    /**
     * 连接是否建立
     */
    private boolean isConnected;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        isConnected = false;
        isAvailable = false;
        this.establishConnection();
    }

    /**
     * 与服务端建立连接
     */
    private void establishConnection() {
        workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new VerifyResponseHandler(NettyClient.this));
                        ch.pipeline().addLast(new RpcResponseHandler(pending));
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        connect(bootstrap, MAX_RETRY);
        try {
            /**
             * 主线程阻塞，等待条件判断
             *  1. 连接是否建立
             *  2. 身份验证是否通过
             */
            countDownLatch = new CountDownLatch(2);
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立连接
     * @param bootstrap
     * @param retry 剩余重连次数
     */
    private void connect(Bootstrap bootstrap, int retry) {
        log.info("尝试与服务地址 [{}:{}] 建立连接...", ip, port);
        bootstrap.connect(ip, port).addListener(future -> {
            if (future.isSuccess()) {
                // 连接通道建立后，主线程满足条件之一
                log.info("连接 [{}:{}] 建立成功！", ip, port);
                isConnected = true;
                countDownLatch.countDown();
                // 初始化通道，同时开始身份验证
                channel = ((ChannelFuture) future).channel();
            } else if (retry == 0) {
                // 通道未建立，则默认身份验证也失败
                log.error("重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                countDownLatch.countDown();
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                log.error("连接失败，第 {} 次重连……", order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }

    @Override
    public RpcFuture sendRequest(RpcRequest request) {
        if (channel != null) {
            RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
            pending.put(request.getRequestId(), rpcFuture);
            log.info("RPC 同步请求[id={}] 已发送", request.getRequestId());
            channel.writeAndFlush(request);
            return rpcFuture;
        }
        log.error("服务端连接尚未建立！");
        return null;
    }

    @Override
    public RpcFuture sendAsyncRequest(RpcRequest request, AsyncRpcCallback callback) {
        if (channel != null) {
            RpcFuture rpcFuture = new RpcFuture(request.getRequestId(), callback);
            pending.put(request.getRequestId(), rpcFuture);
            log.info("RPC 异步请求[id={}] 已发送", request.getRequestId());
            channel.writeAndFlush(request);
            return rpcFuture;
        }
        log.error("服务端连接尚未建立！");
        return null;
    }

    @Override
    public void close(ClientManager manager) {
        workerGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                if (isConnected == true) {
                    log.info("与服务端 [{}:{}] 的连接断开成功！", ip, port);
                }
                manager.doneClose();
            } else {
                log.error("与服务端 [{}:{}] 的连接断开失败！", ip, port);
                manager.doneClose();
            }
        });
    }
}
