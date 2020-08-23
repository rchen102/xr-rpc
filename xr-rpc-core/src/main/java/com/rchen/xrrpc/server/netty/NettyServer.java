package com.rchen.xrrpc.server.netty;

import com.rchen.xrrpc.codec.PacketDecoder;
import com.rchen.xrrpc.codec.PacketEncoder;
import com.rchen.xrrpc.server.TransportServer;
import com.rchen.xrrpc.server.netty.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Map;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
public class NettyServer implements TransportServer {

    private String ip;
    private int port;
    private Map<String, Object> serviceBeanMap;

    public NettyServer(String ip, int port, Map<String, Object> serviceBeanMap) {
        this.ip = ip;
        this.port = port;
        this.serviceBeanMap = serviceBeanMap;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new PacketDecoder());
                            ch.pipeline().addLast(new RpcRequestHandler());
                            ch.pipeline().addLast(new PacketEncoder());
                        }
                    });
            // 启动 RPC Netty 服务器
            ChannelFuture f = bootstrap.bind(ip, port).sync().addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println(new Date() + ": 地址[" + ip + ":" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                }
            });
            // TODO 服务注册
            // 关闭 Netty 服务器
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅地关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
