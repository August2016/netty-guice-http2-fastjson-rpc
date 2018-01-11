package com.easy.guice.rpc.server;

import com.easy.guice.rpc.protocol.RpcRequest;
import com.easy.guice.rpc.server.annotation.RpcService;
import com.google.inject.Injector;
import com.google.inject.spi.LinkedKeyBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * GuiceRpcContext用于hold injector 及 rpc invoker
 * Created by liuchengjun on 2018/1/8.
 */
public class RpcContext {

    private static RpcContext context;
    private Injector injector = null;
    private Map<Class, Object> services = null;
    private RpcInvoker rpcInvoker;

    /**
     * 调用service
     * @param rpcRequest
     */
    public static Object invoke(RpcRequest rpcRequest) throws InvocationTargetException {
        return RpcContext.getContext().getRpcInvoker().invoke(rpcRequest);
    }

    public static RpcContext getContext() {
        return context;
    }


    public static  void init(Injector injector) {
        RpcContext instance = new RpcContext(injector);
        RpcContext.context = instance;
        return;
    }

    private RpcContext(Injector injector) {
        this.injector = injector;
        this.initService();
        this.initInvoker();
    }

    /**
     * 获取rpc service api及实现方法
     *
     * @return
     */
    public void initService() {

        if (services == null) {
            services = new HashMap<>();
        } else {
            return;
        }

        injector.getBindings().forEach((key, binding) -> {
            if (binding instanceof LinkedKeyBinding) {

                Class serviceInterfaceClass = ((LinkedKeyBinding) binding).getKey().getTypeLiteral().getRawType();
                if (serviceInterfaceClass.isInterface()) {

                    boolean isRpcService = serviceInterfaceClass.isAnnotationPresent(RpcService.class);;
                    if (isRpcService) {
                        Object serviceImplInstance = injector.getInstance(serviceInterfaceClass);
                        services.put(serviceInterfaceClass, serviceImplInstance);
                    }
                }
            }
        });
    }

    /**
     * 初始化调用方法
     *
     * @return
     */
    public void initInvoker() {
        RpcInvoker rpcInvoker = new RpcInvoker(services);
        this.rpcInvoker = rpcInvoker;
    }

    public Injector getInjector() {
        return injector;
    }

    public Map<Class, Object> getServices() {
        return services;
    }

    public RpcInvoker getRpcInvoker() {
        return rpcInvoker;
    }
}
