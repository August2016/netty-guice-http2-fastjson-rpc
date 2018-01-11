package com.easy.guice.rpc.server.jetty;

import com.alibaba.fastjson.JSON;
import com.easy.guice.rpc.protocol.RpcRequest;
import com.easy.guice.rpc.protocol.RpcResponse;
import com.easy.guice.rpc.server.RpcContext;
import com.google.common.io.ByteStreams;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuchengjun on 2018/1/11.
 */
public class HttpHandler extends AbstractHandler{

    private static Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        byte[] content = ByteStreams.toByteArray(request.getInputStream());
        RpcRequest rpcRequest = JSON.parseObject(content,RpcRequest.class);

        logger.debug("Receive request:{} ", JSON.toJSONString(rpcRequest));

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            Object result = RpcContext.getContext().getRpcInvoker().invoke(rpcRequest);
            rpcResponse.setResult(result);
        } catch (Throwable t) {
            rpcResponse.setError(t.toString());
            logger.error("RPC Server handle request error", t);
        }

        response.getWriter().write(JSON.toJSONString(rpcResponse));
    }
}
