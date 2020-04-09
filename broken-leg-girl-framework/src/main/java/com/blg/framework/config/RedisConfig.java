package com.blg.framework.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.blg.framework.data.SerializableWrapper;
import com.blg.framework.lock.LockAdvice;
import com.blg.framework.lock.RedisLockOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/2 10:51
 * @Description: Redis配置
 */
@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        RedisSerializer redisSerializer = new RedisSerializer(){
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                if(o instanceof Serializable
                        && !o.getClass().getCanonicalName().startsWith("java.")){
                    SerializableWrapper serializableWrapper = new SerializableWrapper();
                    serializableWrapper.write((Serializable)o);
                    o = serializableWrapper;
                }
                return JSON.toJSONBytes(o, SerializerFeature.WriteClassName);
            }
            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if(bytes==null){
                    return null;
                }
                Object object = JSON.parse(bytes);
                if(object instanceof SerializableWrapper){
                    object = ((SerializableWrapper)object).read();
                }
                return object;
            }
        };
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisLockOp redisLockOp(RedisTemplate redisTemplate){
        RedisLockOp redisLockOp = new RedisLockOp();
        redisLockOp.setRedisTemplate(redisTemplate);
        return redisLockOp;
    }

    @Bean
    public LockAdvice lockAdvice(){
        return new LockAdvice();
    }

}
