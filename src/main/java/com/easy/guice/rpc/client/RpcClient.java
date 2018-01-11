package com.easy.guice.rpc.client;

import com.alibaba.fastjson.JSON;
import com.easy.guice.rpc.client.httpClient.HttpClientInterface;
import com.easy.guice.rpc.client.proxy.RpcInvocationHandler;
import com.easy.guice.rpc.protocol.RpcRequest;
import com.easy.guice.rpc.protocol.RpcResponse;
import com.easy.guice.rpc.zk.ServiceDiscovery;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuchengjun on 2018/1/10.
 */
public class RpcClient {

    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private static ConcurrentHashMap<String, AtomicLong> INVOKE_FREQUENCY_MAP = new ConcurrentHashMap();

    private ServiceDiscovery serviceDiscovery;

    private HttpClientInterface httpClient;

    public RpcClient(ServiceDiscovery serviceDiscovery, HttpClientInterface httpClient) {
        this.serviceDiscovery = serviceDiscovery;
        serviceDiscovery.connect();
        this.httpClient = httpClient;
    }

    public RpcResponse callRemoteService(RpcRequest request) {

        AtomicLong invokeFrequency = INVOKE_FREQUENCY_MAP.get(request.getClassName());
        if (invokeFrequency == null) {

            invokeFrequency = new AtomicLong(0);
            INVOKE_FREQUENCY_MAP.put(request.getClassName(), invokeFrequency);
        }

        long totalInvokeTimes = invokeFrequency.getAndAdd(1);

        ArrayListMultimap map = this.getServiceDiscovery().getCurrentServiceMap();

        List<String> remoteServiceAddressList = map.get(request.getClassName());

        if (CollectionUtils.isEmpty(remoteServiceAddressList)) {
            throw new RuntimeException("remote service not found");
        }

        Long index = totalInvokeTimes % remoteServiceAddressList.size();

        String remoteServiceAddress = remoteServiceAddressList.get(index.intValue());

        byte[] bytes = JSON.toJSONBytes(request);

        byte[] result = httpClient.post("http://" + remoteServiceAddress, bytes);

        return JSON.parseObject(result, RpcResponse.class);

    }

    public <T> T create(Class<T> clazz) {
        T proxyInstance = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
            new Class[]{clazz}, new RpcInvocationHandler(clazz, this));
        return proxyInstance;
    }


    private ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }
}
