package com.rchen.xrrpc.client.netty;

import com.rchen.xrrpc.client.RpcFuture;
import com.rchen.xrrpc.client.TestThread;
import com.rchen.xrrpc.client.TransportClient;
import com.rchen.xrrpc.client.netty.handler.RpcResponseHandler;
import com.rchen.xrrpc.codec.PacketDecoder;
import com.rchen.xrrpc.codec.PacketEncoder;
import com.rchen.xrrpc.protocol.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class NettyClient implements TransportClient {
    private String ip;
    private int port;

    private Channel channel;

    private static final int MAX_RETRY = 5;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new RpcResponseHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        connect(bootstrap, MAX_RETRY);
    }

    /**
     *
     * @param bootstrap
     * @param retry 剩余重连次数
     */
    private void connect(Bootstrap bootstrap, int retry) {
        ChannelFuture f = bootstrap.connect(ip, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 服务[" + ip + ":" + port + "]连接成功!");
                channel = ((ChannelFuture) future).channel();
                new Thread(new TestThread(channel)).start();
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap,retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }

    @Override
    public RpcFuture sendRequest(Packet request) {
        channel.writeAndFlush(request);
        return null;
    }
}
