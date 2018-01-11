package com.easy.guice.rpc.client.httpClient;

import io.netty.handler.codec.http.HttpHeaderValues;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public class HttpImpl implements HttpClientInterface{

    HttpClient httpClient;

    public HttpImpl() {

        httpClient = new HttpClient(null);

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpClient.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public byte[] post(String url, byte[] body){
        ContentResponse response = null;
        try {
            response = httpClient.newRequest(url)
                .content(new BytesContentProvider(HttpHeaderValues.APPLICATION_JSON.toString(),body))
                .timeout(30, TimeUnit.SECONDS)
                .send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response.getContent();
    }
}
