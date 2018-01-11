package com.easy.guice.rpc.server.annotation;


import java.lang.annotation.*;

/**
 * Rpc service annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@Documented
public @interface RpcService {
}
