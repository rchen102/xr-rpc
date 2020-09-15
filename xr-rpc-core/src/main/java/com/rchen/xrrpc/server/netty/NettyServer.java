package com.rchen.xrrpc.server.netty;

import com.rchen.xrrpc.codec.PacketDecoder;
import com.rchen.xrrpc.codec.PacketEncoder;
import com.rchen.xrrpc.protocol.Spliter;
import com.rchen.xrrpc.server.RpcServer;
import com.rchen.xrrpc.server.TransportServer;
import com.rchen.xrrpc.server.netty.handler.AuthHandler;
import com.rchen.xrrpc.server.netty.handler.RpcRequestHandler;
import com.rchen.xrrpc.server.netty.handler.VerifyRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
public class NettyServer implements TransportServer {

    private String ip;
    private int port;
    private Map<String, Object> serviceBeanMap;

    public NettyServer(String ip, int port, Map<String, Object> serviceBeanMap) {
        this.ip = ip;
        this.port = port;
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    public void start(RpcServer server) {
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
                            ch.pipeline().addLast(new Spliter());
                            ch.pipeline().addLast(new PacketDecoder());
                            ch.pipeline().addLast(new VerifyRequestHandler());
                            ch.pipeline().addLast(new AuthHandler());
                            ch.pipeline().addLast(new RpcRequestHandler(serviceBeanMap));
                            ch.pipeline().addLast(new PacketEncoder());
                        }
                    });
            // 启动 RPC Netty 服务器
            ChannelFuture f = bootstrap.bind(ip, port).sync().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("地址 [{}:{}] 绑定成功!", ip, port);
                    log.info("开始服务注册...");
                    server.registerService();
                } else {
                    log.error("端口 [{}] 绑定失败!", port);
                }
            });
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
