package com.blg.framework.lock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.blg.framework.Java;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现分布式锁，确保所有应用节点的Redis都连接到同一个Redis库
 * @author lujijiang
 */
public class RedisLockOp implements LockOp,InitializingBean {
    /**
     * ID池，用来标记线程
     */
    private ThreadLocal<String> idHolder = new ThreadLocal<>();
    /**
     * Redis模板
     */
    private RedisTemplate redisTemplate;
    /**
     * 加锁尝试次数（默认一次，快速失败）
     */
    private int maxAttempts = 10;
    /**
     * 加锁尝试间隔时间（默认1毫秒）
     */
    private long attemptIntervalMilliseconds = 1L;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setAttemptIntervalMilliseconds(long attemptIntervalMilliseconds) {
        this.attemptIntervalMilliseconds = attemptIntervalMilliseconds;
    }

    /**
     * 尝试加锁
     * @param obj 锁定对象
     * @param timeout 超时时间（秒数）
     * @param callable 执行闭包
     * @param <V>
     * @return
     */
    public <V> V tryLock(Object obj,int timeout, Callable<V> callable) {
        String json = getClass().getSimpleName()+":"+ JSON.toJSONString(obj, SerializerFeature.WriteClassName,SerializerFeature.NotWriteRootClassName);
        String id = idHolder.get();
        if(id==null){
            id = UUID.randomUUID().toString();
            idHolder.set(id);
        }
        int maxAttempts = this.maxAttempts;
        while (maxAttempts-- > 0) {
            try {
                if (this.redisTemplate.opsForValue().setIfAbsent(json, id,timeout,TimeUnit.SECONDS)) {
                    return callable.call();  //如果成功插入说明该资源没有被占用，可执行
                } else if(Objects.equals(id,this.redisTemplate.opsForValue().get(json))){
                    return callable.call();  //如果获得的值跟id相同，则表明是同一个线程在占用资源，可以执行
                } else {
                    TimeUnit.MILLISECONDS.sleep(this.attemptIntervalMilliseconds);  //如果没有匹配上，则重试
                }
            } catch (Exception e) {
                throw Java.unchecked(e);
            } finally {
                String scriptText = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> script = new DefaultRedisScript<>(scriptText,Long.class);
                this.redisTemplate.execute(script, Collections.singletonList(json), id);
            }
        }
        throw new IllegalStateException("Failed to get distributed lock for:\r\n" + json);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redisTemplate, "The redisTemplate should not be null");
        Locks.setLockOp(this);
    }
}
