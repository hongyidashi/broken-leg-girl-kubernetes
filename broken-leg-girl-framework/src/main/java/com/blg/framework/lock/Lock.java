package com.blg.framework.lock;

import java.lang.annotation.*;

/**
 * 方法级分布式锁，默认锁定时间三分钟
 * @author lujijiang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    int timeout() default 180;
}