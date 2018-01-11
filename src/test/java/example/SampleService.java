package example;


import com.easy.guice.rpc.server.annotation.RpcService;

/**
 * Created by liuchengjun on 2018/1/8.
 */
@RpcService
public interface SampleService {
     void setA();
     void setB(String a);
     String setC(String c);

}
