package com.easy.guice.rpc.client.module;

import com.easy.guice.rpc.client.RpcClient;
import com.easy.guice.rpc.client.httpClient.Http2Impl;
import com.easy.guice.rpc.client.httpClient.HttpClientInterface;
import com.easy.guice.rpc.zk.ServiceDiscovery;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by liuchengjun on 2018/1/10.
 */
public class RpcClientModule extends AbstractModule {

    List<Class> serviceInterfaceList;

    String zkAddress;

    public RpcClientModule(String zkAddress, List<Class> serviceInterfaceList) {
        this.zkAddress = zkAddress;
        this.serviceInterfaceList = serviceInterfaceList;
    }

    @Override
    protected void configure() {

        RpcClient rpcClient = new RpcClient(getServiceDiscovery(),getHttpClient());
        bind(RpcClient.class).toInstance(rpcClient);
        if (!CollectionUtils.isEmpty(serviceInterfaceList)) {
            serviceInterfaceList.forEach(clazz -> {
                bind(clazz).toInstance(rpcClient.create(clazz));
            });
        }
    }

    @Provides
    private ServiceDiscovery getServiceDiscovery() {
        return new ServiceDiscovery(zkAddress);
    }

    @Provides
    private HttpClientInterface getHttpClient() {
        return new Http2Impl();
    }
}
