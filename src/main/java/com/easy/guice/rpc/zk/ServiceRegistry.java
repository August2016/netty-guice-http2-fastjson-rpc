package com.easy.guice.rpc.zk;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务注册
 *
 * @author huangyong
 * @author luxiaoxun
 */
public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private String registryAddress;

    private List<String> serviceList = new ArrayList<>();

    ZkClient zkClient;

    private String serviceAddress;

    public ServiceRegistry(String registryAddress, String serviceAddress, List<String> serviceList) {
        this.registryAddress = registryAddress;
        this.serviceList = serviceList;
        this.serviceAddress = serviceAddress;
    }

    synchronized public void register() {
        connectServer();
    }

    private void connectServer() {

        zkClient = new ZkClient(registryAddress, Constant.TIMEOUT);

        addRootNode();

        createNode();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                zkClient.close();
                System.out.println("close zk connection");
            }
        }));
    }

    private void addRootNode() {
        try {
            Boolean isExist = zkClient.exists(Constant.BASE_DIR);
            if (!isExist) {
                zkClient.createEphemeral(Constant.BASE_DIR, new byte[0]);
            }
        } catch (Exception e) {
            logger.error("zk插入数据失败", e);
        }
    }

    private void createNode() {

        String serviceListStr = serviceList.stream().collect(Collectors.joining(","));

        String path = Constant.NODE_FULL_NAME_PRE + serviceAddress;
        try {
            zkClient.createEphemeral(Constant.NODE_FULL_NAME_PRE + serviceAddress, serviceListStr);
        } catch (Exception e) {
            logger.error("zk插入数据失败", e);
        }
        logger.debug("create zookeeper node ({} => {})", path, serviceListStr);
    }
}