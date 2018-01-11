package example;

import com.easy.guice.rpc.server.Application;

import java.util.Arrays;

/**
 * Created by liuchengjun on 2018/1/10.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        Application.args(args).modules(Arrays.asList(new ExampleModule())).start();
    }
}
