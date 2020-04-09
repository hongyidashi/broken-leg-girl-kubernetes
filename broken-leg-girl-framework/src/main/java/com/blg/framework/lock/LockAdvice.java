package com.blg.framework.lock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.blg.framework.Java;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 分布式锁切面
 * @author lujijiang
 */
@Aspect
public class LockAdvice {
    @Around("@annotation(com.blg.framework.lock.Lock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable{
        String method = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String key = method+":"+JSON.toJSONString(args,SerializerFeature.WriteClassName,SerializerFeature.NotWriteRootClassName);
        Lock lock = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Lock.class);
        return Locks.tryLock(key,lock.timeout(),()->{
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw Java.unchecked(e);
            }
        });
    }
}
