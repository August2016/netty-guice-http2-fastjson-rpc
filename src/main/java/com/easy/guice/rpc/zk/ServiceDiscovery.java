package com.easy.guice.rpc.zk;

import com.google.common.collect.ArrayListMultimap;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务发现
 */
public class ServiceDiscovery {

    private ZkClient zkClient;

    private String serviceAddress;

    private volatile ArrayListMultimap currentServiceMap = ArrayListMultimap.create();

    private AtomicInteger currentVersion = new AtomicInteger(0);

    public ServiceDiscovery(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void connect() {
        zkClient = new ZkClient(serviceAddress, Constant.TIMEOUT);

        zkClient.subscribeChildChanges(Constant.BASE_DIR, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                updateServiceMap(currentChilds);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                zkClient.close();
                System.out.println("close zk connection");
            }
        }));

        initServiceMap();
    }

    private void updateServiceMap(List<String> currentChilds) {

        int version = currentVersion.getAndAdd(1);

        if (CollectionUtils.isEmpty(currentChilds)) {
            updateVersionAndServiceMap(version,ArrayListMultimap.create());
        }

        ArrayListMultimap arrayListMultimap = ArrayListMultimap.create();
        currentChilds.stream().forEach(currentChild -> {

            if (version != currentVersion.get() - 1) {
                return;
            }

            String data = zkClient.readData(Constant.BASE_DIR + "/" + currentChild);
            String[] serviceNameList = data.split(",");
            String serviceLocation = currentChild.replace(Constant.NODE_NAME_PRE,"");
            Arrays.asList(serviceNameList).forEach(serviceName -> {
                arrayListMultimap.put(serviceName, serviceLocation);
            });
        });

        updateVersionAndServiceMap(version,arrayListMultimap);
    }

    private void initServiceMap() {
        List<String> currentChilds = zkClient.getChildren(Constant.BASE_DIR);
        updateServiceMap(currentChilds);
    }

    private synchronized void updateVersionAndServiceMap(int version , ArrayListMultimap serviceMap) {
        if (version != currentVersion.get() - 1) {
            return;
        }

        currentServiceMap = serviceMap;
    }

    public ArrayListMultimap getCurrentServiceMap() {
        return currentServiceMap;
    }

    public void setCurrentServiceMap(ArrayListMultimap currentServiceMap) {
        this.currentServiceMap = currentServiceMap;
    }
}
