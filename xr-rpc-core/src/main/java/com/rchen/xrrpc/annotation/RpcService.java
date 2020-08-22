package com.rchen.xrrpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解服务的实现类
 *
 * @Author : crz
 * @Date: 2020/8/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 实现的接口
     */
    Class<?> value();

    /**
     * 服务的版本号
     */
    String version() default "1.0";
}
