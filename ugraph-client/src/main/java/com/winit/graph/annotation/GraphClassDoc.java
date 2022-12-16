package com.winit.graph.annotation;

import java.lang.annotation.*;

/**
 * 类描述信息的注解
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
@Documented
public @interface GraphClassDoc {
    String value();
}
