package com.easy.guice.rpc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RpcServer
 * start 方法返回一个thread句柄等待netty server关闭
 */
public class NettyServer {

    private static NettyServer rpcServer;

    public static NettyServer bindPort(int port) {
        if (rpcServer == null) {
            rpcServer = new NettyServer();
        }
        rpcServer.port = port;
        return rpcServer;
    }

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private int port;

    private NettyServer() {
    }

    public Thread start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new Http2ServerInitializer(null));

        try {
            ChannelFuture future = bootstrap.bind(port).sync();

            Thread t = new Thread(() -> {
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {

                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            });

            t.start();
            logger.info("Server started on port {}", port);

            return t;
        } catch (InterruptedException e) {
            logger.error("服务启动失败", e);
            throw new RuntimeException(e);
        }
    }
}
