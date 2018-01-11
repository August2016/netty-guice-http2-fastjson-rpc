# netty-guice-rpc
netty-guice-rpc参考 [https://gitee.com/huangyong/rpc](https://gitee.com/huangyong/rpc)
以及 [https://github.com/August2016/NettyRpc](https://github.com/August2016/NettyRpc)
两位作者的实现

#### 主要技术:

- guice
- netty
- http2.0
- fastjson
- jetty-client-http2.0
- zk

#### 测试运行方案

例子是一个简单的客户端请求，能够统计100秒访问次数。
目前单台机器同时开多个应用的情况能够达到每秒3-4万次请求。

```
Thu Jan 11 21:14:28 CST 2018
Thu Jan 11 21:16:08 CST 2018
3482611次请求耗时100秒
```

1、安装zk
2、修改配置
配置test application.properties 中zookeeper地址
3、运行 Server
4、运行 Client

### 配置优先级
配置使用guice自带的功能实现:
```
Names.bindProperties(binder(), bootstrapProperties);
```

启动参数（如:--server.port=80）> application.{env}.properties > application.properties

下一步优化方案:

- 异常处理优化
- 调用优先级优化（目前是轮询）
- 服务监控

联系方式: lcj_up@sina.com