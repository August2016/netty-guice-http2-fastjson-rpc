package com.easy.guice.rpc.client.proxy;

import com.easy.guice.rpc.client.RpcClient;
import com.easy.guice.rpc.protocol.RpcRequest;
import com.easy.guice.rpc.protocol.RpcResponse;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;


public class RpcInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);
    private Class clazz;
    private RpcClient rpcClient;

    public RpcInvocationHandler(Class clazz, RpcClient rpcClient) {
        this.clazz = clazz;
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        /*Type genericReturnType = method.getGenericReturnType();*/
        if (logger.isDebugEnabled()) {
            logger.debug("invoke class: {}", clazz);
            logger.debug("invoke method: {}", method.getName());

            for (int i = 0; i < method.getParameterTypes().length; ++i) {
                logger.debug(method.getParameterTypes()[i].getName());
            }
            for (int i = 0; i < args.length; ++i) {
                logger.debug(args[i].toString());
            }
        }

        RpcResponse response =  rpcClient.callRemoteService(request);

        return response.getResult();
    }

    private RpcRequest createRequest(Class clazz, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameters(args);

        Class[] parameterTypes = method.getParameterTypes();
        request.setParameterTypes(parameterTypes);
        if (logger.isDebugEnabled()) {
            logger.debug("request class: {}", clazz);
            logger.debug("request method: {}", method.getName());
            for (int i = 0; i < parameterTypes.length; ++i) {
                logger.debug("request param[{}], type: {}", i + 1, parameterTypes[i].getName());
            }
            for (int i = 0; i < args.length; ++i) {
                logger.debug(args[i].toString());
            }
        }

        return request;
    }

    /*private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }*/

}
