package com.pul.demo.auth;

import com.pul.demo.config.JwtConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Import({JwtConfig.class})
public class LogicAutoConfiguration {
    @Order(10)
    @Bean
    @ConditionalOnBean(JwtConfig.class)
    public DefaultJwtLogic defaultJwtLogic(JwtConfig jwtConfig) {
        return new DefaultJwtLogic(jwtConfig);
    }

    @Order(1)
    @Bean
    public DefaultLogic defaultLogic() {
        return new DefaultLogic();
    }

}
