package com.winit.graph.annotation;

import java.lang.annotation.*;

/**
 * 方法描述信息的注解
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
@Documented
public @interface GraphMethodDoc {
    String value();
}
