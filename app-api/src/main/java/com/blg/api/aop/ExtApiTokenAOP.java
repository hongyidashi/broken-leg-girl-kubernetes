package com.blg.api.aop;

import com.blg.api.annotation.ExtApiToken;
import com.blg.api.vo.reqvo.RequestVo;
import com.blg.framework.lock.RedisLockOp;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ExtApiTokenAOP {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisLockOp redisLockOp;

    @Pointcut("execution(* com.blg.*.controller.*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ExtApiToken extApiToken = signature.getMethod().getDeclaredAnnotation(ExtApiToken.class);
        if (extApiToken != null) { //如果添加注解则需要判断是否重复提交
            Object[] args = joinPoint.getArgs();
            String token = ((RequestVo) args[0]).getToken();
            //如果之前执行过,并且离过期还有一段时间(100毫秒)，那么直接返回结果
            return redisLockOp.tryLock(token, 10, () -> {
                if (redisTemplate.hasKey("token:" + token) &&
                        redisTemplate.getExpire("token:" + token, TimeUnit.MILLISECONDS) > 100) {
                    log.warn("重复进行修改或提交操作");
                    return redisTemplate.opsForValue().get("token:" + token);
                } else {
                    //否则之前没有执行过，那么执行，然后将数据存放到redis中，并设置过期时间
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    try {
                        Object result = joinPoint.proceed();
                        redisTemplate.opsForValue().set("token:" + token, result, 24, TimeUnit.HOURS);
                        return result;
                    } catch (Throwable e) {
                        throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
                    }
                }
            });
        }
        return joinPoint.proceed();
    }
}
