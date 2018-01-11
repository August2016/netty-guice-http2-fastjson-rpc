package com.easy.guice.rpc.zk;


public interface Constant {
    int TIMEOUT = 5000;
    String BASE_DIR = "/netty-guice-rpc";
    String NODE_NAME_PRE = "service-";
    String NODE_FULL_NAME_PRE = BASE_DIR + "/" + NODE_NAME_PRE;
}
