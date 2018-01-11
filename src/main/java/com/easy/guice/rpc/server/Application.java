package com.easy.guice.rpc.server;

import com.easy.guice.rpc.server.module.PropertiesModule;
import com.easy.guice.rpc.server.netty.NettyServer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.easy.guice.rpc.zk.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 入口类
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    private static Application application;

    private  String default_server_port = "8080";
    private  String server_port_property = "server.port";

    private  String register_address_property = "zk.address";

    private  String default_stage = "";
    private  String stage_property = "applidation.env";

    private  String default_property_path = "/applidation.properties";
    private  String stage_property_path = "/applidation.{0}.properties";

    private  String[] args;

    private  List<Module> moduleList;

    public static Application args(String[] args) {
        if (application == null) {
            application = new Application();
        }
        application.args = args;
        return application;
    }

    public static Application modules( List<Module> moduleList) {
        if (application == null) {
            application = new Application();
        }
        application.moduleList = moduleList;
        return application;
    }

    public void start() {
        logger.info("staring application");

        Properties properties = mergeProperties(args);

        List<Module> modules = new ArrayList<>();
        modules.add(new PropertiesModule(properties));
        modules.addAll(moduleList);

        Injector injector = Guice.createInjector(modules);

        RpcContext.init(injector);

        int port = Integer.parseInt(properties.getProperty(server_port_property));
        Thread t = NettyServer.bindPort(port).start();

        String registerAddress = properties.getProperty(register_address_property);

        if(registerAddress != null && registerAddress.length() >0 ) {

            List<String> serviceList = RpcContext.getContext().getServices().keySet()
                .stream().map(clazz -> clazz.getName()).collect(Collectors.toList());

            try {
                String localHostAddress = InetAddress.getLocalHost().getHostAddress();
                String localServerAddress = localHostAddress + ":" + port;
                ServiceRegistry registry = new ServiceRegistry(registerAddress,localServerAddress,serviceList);
                registry.register();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        logger.info("application started");

        try {
            t.join();
            logger.info("application started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Application() {
    }

    public Properties mergeProperties(String[] args) {

        Properties properties = loadPropertiesFromFile(default_property_path);

        Properties paramProperties = loadPropertiesFromPrams(args);

        String stage = null;
        String port = null;
        if (properties.containsKey(stage_property)) {
            stage = properties.getProperty(stage_property);
        }
        if (properties.containsKey(server_port_property)) {
            port = properties.getProperty(server_port_property);
        }

        if (paramProperties.containsKey(stage_property)) {
            stage = properties.getProperty(stage_property);
        }
        if (paramProperties.containsKey(server_port_property)) {
            port = properties.getProperty(server_port_property);
        }

        if (port == null) {
            properties.setProperty(server_port_property, default_server_port);
        }

        if (stage == null) {
            properties.setProperty(stage_property, default_stage);
        }

        if (stage != null) {
            String stagePropertyFile = MessageFormat.format(stage_property_path, stage);
            Properties stageProperties = loadPropertiesFromFile(stagePropertyFile);
            properties.putAll(stageProperties);
        }

        properties.putAll(paramProperties);

        return properties;

    }

    public Properties loadPropertiesFromPrams(String[] args) {

        Properties properties = new Properties();
        if (args != null || args.length > 0) {
            Arrays.asList(args).stream()
                .filter(arg -> arg != null && arg.startsWith("--"))
                .map(arg -> arg.replaceFirst("--", ""))
                .forEach(arg -> {
                    String[] namaValuePair = arg.split(":", 2);
                    properties.put(namaValuePair[0], namaValuePair[1]);
                });
        }

        return properties;
    }

    public Properties loadPropertiesFromFile(String path) {


        InputStream in = this.getClass().getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(in);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
