package com.easy.guice.rpc.client.httpClient;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public interface HttpClientInterface {
    byte[] post(String url, byte[] body);
}
