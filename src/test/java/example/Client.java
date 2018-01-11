package example;

import com.easy.guice.rpc.client.RpcClient;
import com.easy.guice.rpc.client.module.RpcClientModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public class Client {
    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new RpcClientModule("10.211.55.4:2181",Arrays.asList(SampleService.class)));
        SampleService sampleService = injector.getInstance(SampleService.class);

        System.out.println(new Date());

        AtomicLong atomicLong = new AtomicLong(0);
        long start = System.currentTimeMillis();

        for (int i = 0; i < 8; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i > -1; i++) {
                        atomicLong.addAndGet(1);
                        String result = sampleService.setC("i" + i);
                        /*System.out.println("requeest:" + i + ",-----result:" + result + "");*/
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println(new Date());
        System.out.println(""+atomicLong.get()+"次请求耗时"+(end-start)/1000+"秒");

    }
}
