package com.itcast.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig  extends CachingConfigurerSupport {


    /**
     * redis的序列化器
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object,Object>  redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<Object,Object>  redisTemplate=new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return  redisTemplate;


    }
}
