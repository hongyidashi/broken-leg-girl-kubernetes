package com.blg.framework.jpa.support;

import java.lang.annotation.*;

/**
 * 给实体对象添加注释
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Comment {
    String value();
}
