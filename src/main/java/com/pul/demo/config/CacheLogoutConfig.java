package com.pul.demo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnBean(JwtConfig.class)
@ConditionalOnProperty(name = "auth.jwt.logout",havingValue = "true")
public class CacheLogoutConfig {
    public static final String LOGOUT_CACHE_MANAGER = "logoutCacheManager";

    @Bean(LOGOUT_CACHE_MANAGER)
    @ConditionalOnProperty(value="cache.mode",havingValue = "caffeine",matchIfMissing = true)
    public CacheManager caffeinelogoutCacheManager(JwtConfig jwtConfig) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // Caffeine配置
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 最后一次写入后经过固定时间过期
                .expireAfterWrite(Duration.ofMillis(jwtConfig.getLogoutKeep()))
                // 缓存的最大条数
                .maximumSize(100_000);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }


    @Bean(LOGOUT_CACHE_MANAGER)
    @ConditionalOnProperty(value="cache.mode",havingValue = "redis",matchIfMissing=false)
    public CacheManager redisCacheManager(JwtConfig jwtConfig, LettuceConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                serializerObjectMapper());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.entryTtl(Duration.ofMillis(jwtConfig.getLogoutKeep()));
        config = config
                // 键序列化方式 redis字符串序列化
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                // 值序列化方式 简单json序列化
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(genericJackson2JsonRedisSerializer));
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config).build();
    }

    private ObjectMapper serializerObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
