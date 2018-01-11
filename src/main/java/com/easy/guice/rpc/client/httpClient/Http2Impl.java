package com.easy.guice.rpc.client.httpClient;

import io.netty.handler.codec.http.HttpHeaderValues;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public class Http2Impl implements HttpClientInterface {

    HttpClient httpClient;

    public Http2Impl() {
        HttpClientTransport transport = new HttpClientTransportOverHTTP2(
            new HTTP2Client());

        httpClient = new HttpClient(transport, null);

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
