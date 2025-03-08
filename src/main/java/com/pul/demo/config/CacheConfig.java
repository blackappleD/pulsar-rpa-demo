package com.pul.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {

    /**
     * 缓存模式，支持redis和caffeine，默认是caffeine
     */
    private String mode="caffeine";
    
    /**
     * 缓存过期时间,默认时间是5分钟
     */
    private Long expire = 5 * 60L;

}
