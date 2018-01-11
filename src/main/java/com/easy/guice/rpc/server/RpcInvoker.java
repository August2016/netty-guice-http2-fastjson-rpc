package com.easy.guice.rpc.server;

import com.alibaba.fastjson.JSON;
import com.easy.guice.rpc.protocol.RpcRequest;
import com.easy.guice.rpc.protocol.RpcResponse;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RpcInvoker {

    private static Logger logger = LoggerFactory.getLogger(RpcInvoker.class);

    private Map<String, Object> serviceMap = new HashMap<>();
    private Map<String, FastClass> serviceSchemaMap = new HashMap<>();

    public RpcInvoker(Map<Class, Object> services) {
        buildMethodMap(services);
    }

    /**
     * 解析方法调用路径map
     *
     * @param services
     */
    public void buildMethodMap(Map<Class, Object> services) {
        services.forEach((face, impl) -> {
            FastClass serviceFastClass = FastClass.create(face);
            serviceSchemaMap.put(face.getName(),serviceFastClass);
            serviceMap.put(face.getName(),impl);
        });
    }

    /**
     * 方法调用
     * @param rpcRequest
     */
    public Object invoke(RpcRequest rpcRequest) throws InvocationTargetException {
        Object service = serviceMap.get(rpcRequest.getClassName());
        FastClass serviceInterfaceSchema = serviceSchemaMap.get(rpcRequest.getClassName());
        FastMethod fastMethod = serviceInterfaceSchema.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return fastMethod.invoke(service, rpcRequest.getParameters());
    }

    /**
     * 方法调用
     * @param content 请求参数
     */
    public RpcResponse invoke(String content) {

        RpcRequest rpcRequest = JSON.parseObject(content, RpcRequest.class);

        logger.debug("Receive request:{} ", JSON.toJSONString(rpcRequest));

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setRequestId(UUID.randomUUID().toString());
        try {
            Object result = RpcContext.getContext().getRpcInvoker().invoke(rpcRequest);
            rpcResponse.setResult(result);
        } catch (Throwable t) {
            rpcResponse.setError(t.toString());
            logger.error("RPC Server handle request error", t);
        }

        return rpcResponse;

    }

    public static void main(String[] args) {

    }
}
