package com.rchen.xrrpc.client.netty;

import com.rchen.xrrpc.client.AsyncRpcCallback;
import com.rchen.xrrpc.client.RpcFuture;
import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.netty.handler.RpcResponseHandler;
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
public class NettyClient implements TransportClient {

    private static final int MAX_RETRY = 5;

    private String ip;
    private int port;

    /**
     * 连接建立后的 channel
     */
    private Channel channel;

    /**
     * 尚未收到回复的 Request 的 Future
     * <key = requestId, value = RpcFuture>
     */
    private Map<String, RpcFuture> pending = new ConcurrentHashMap<>();

    /**
     * 用于确保连接在 RPC 调用前先建立
     */
    private CountDownLatch countDownLatch;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        countDownLatch = new CountDownLatch(1);
        this.establishConnection();
    }

    /**
     * 与服务端建立连接
     */
    private void establishConnection() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
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
                        ch.pipeline().addLast(new RpcResponseHandler(pending));
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        connect(bootstrap, MAX_RETRY);
        try {
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
        try {
            bootstrap.connect(ip, port).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务端 [{}:{}] 连接成功!", ip, port);
                    channel = ((ChannelFuture) future).channel();
                    /**
                     * 连接建立后取消主线程阻塞
                     */
                    countDownLatch.countDown();
                } else if (retry == 0) {
                    log.error("重试次数已用完，放弃连接！");
                    System.exit(1);
                } else {
                    // 第几次重连
                    int order = (MAX_RETRY - retry) + 1;
                    // 本次重连的间隔
                    int delay = 1 << order;
                    log.error("连接失败，第 {} 次重连……", order);
                    bootstrap.config().group().schedule(() -> connect(bootstrap,retry - 1), delay, TimeUnit
                            .SECONDS);
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public RpcFuture sendRequest(RpcRequest request) {
        if (channel != null) {
            RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
            pending.put(request.getRequestId(), rpcFuture);
            channel.writeAndFlush(request);
            return rpcFuture;
        }
        log.error("sendRequest(): 服务端连接未建立");
        return null;
    }

    @Override
    public RpcFuture sendAsyncRequest(RpcRequest request, AsyncRpcCallback callback) {
        if (channel != null) {
            RpcFuture rpcFuture = new RpcFuture(request.getRequestId(), callback);
            pending.put(request.getRequestId(), rpcFuture);
            channel.writeAndFlush(request);
            return rpcFuture;
        }
        log.error("sendAsyncRequest(): 服务端连接未建立");
        return null;
    }
}
