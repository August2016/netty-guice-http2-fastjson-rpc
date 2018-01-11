package com.easy.guice.rpc.protocol;

/**
 * request body
 */
public class RpcRequest {
    private String requestId;

    /**
     * classname和method可以定位到服务端执行的service
     */
    private String className;
    private String methodName;

    /**
     * 反序列化后可以直接执行
     */
    private Object[] parameters;
    private Class<?>[] parameterTypes;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}