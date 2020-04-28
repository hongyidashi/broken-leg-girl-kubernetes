package com.blg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/2 16:57
 * @Description: 自定义注解，在需要Token方式来解决重复提交的问题时候使用
 */
@Target(value = ElementType.METHOD) //只能够用在方法上
@Retention(RetentionPolicy.RUNTIME) //在运行期的接在阶段被加载到Class对象中，可以通过反射拿到值进行判断
public @interface ExtApiToken {
}
