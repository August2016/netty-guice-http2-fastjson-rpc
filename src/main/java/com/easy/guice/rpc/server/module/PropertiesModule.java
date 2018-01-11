package com.easy.guice.rpc.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.Properties;

/**
 * 资源文件
 * Created by liuchengjun on 2018/1/8.
 */
public class PropertiesModule extends AbstractModule {

    private Properties bootstrapProperties;

    public PropertiesModule(Properties bootstrapProperties) {
        this.bootstrapProperties = bootstrapProperties;
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), bootstrapProperties);

    }
}
