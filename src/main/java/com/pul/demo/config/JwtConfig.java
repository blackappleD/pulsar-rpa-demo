package com.pul.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ConditionalOnProperty(value = "auth.jwt.enable", havingValue = "true")
@ConfigurationProperties("auth.jwt")
@Getter
@Setter
public class JwtConfig {
	private Boolean enable;

	private String secret = UUID.randomUUID().toString();
	private String prefix = "Bearer ";

	private String loginIdAlias;

	private long expire = 5L * 60 * 1000;

	private long logoutKeep = 5L * 60 * 1000;

	private Boolean logout;
}
