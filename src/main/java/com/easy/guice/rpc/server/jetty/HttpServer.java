package com.easy.guice.rpc.server.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private static HttpServer instance;

    private Server server;

    private Integer port;

    public static HttpServer bindPort(int port) {
        if (instance == null) {
            instance = new HttpServer();
        }

        instance.port = port;

        return instance;
    }

    public static void start() {

        if (instance == null) {
            instance = new HttpServer();
        }

        instance.server =  new Server(createThreadPool());
        ServerConnector connector = new ServerConnector(instance.server);
        connector.setPort(instance.port);
        connector.setIdleTimeout(30000);
        instance.server.setConnectors(new ServerConnector[]{connector});

        instance.server.setStopAtShutdown(true);
        instance.server.setHandler(new HttpHandler());

        try {
            instance.server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpServer() {
    }

    private static ThreadPool createThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(10);
        threadPool.setMaxThreads(1000);

        return threadPool;
    }

    public static HttpServer getInstance() {
        return instance;
    }

    public Server getServer() {
        return server;
    }
}
