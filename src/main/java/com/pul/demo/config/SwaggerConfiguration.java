package com.pul.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    private static final String AUTH_KEY = "JWT Secuity";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(AUTH_KEY))
                .components(new Components().addSecuritySchemes(AUTH_KEY,
                        new SecurityScheme().name("jwt").type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT")));
    }

//    @Bean
//    public ReturnTypeParser returnTypeParser() {
//        return new ReturnTypeParser() {
//            @Override
//            public Type getReturnType(MethodParameter methodParameter) {
//                if (methodParameter.getDeclaringClass().isAnnotationPresent(DgResponseBody.class)
//                        || methodParameter.getContainingClass().isAnnotationPresent(DgResponseBody.class)
//                        || methodParameter.getMethodAnnotation(DgResponseBody.class) != null) {
//                    if (methodParameter.getMethodAnnotation(DgResponseBodyIgnore.class) == null) {
//                        // 检查returntype的类型
//                        var m = methodParameter.getMethod();
//                        if (m != null) {
//                            var returnType = m.getGenericReturnType();
//                            if (Void.TYPE.equals(returnType)) {
//                                return DgHttpResponseDto.class;
//                            }
//                            return TypeUtils.parameterize(DgHttpResponseDto.class, returnType);
//                        }
//                    }
//
//                }
//                return ReturnTypeParser.super.getReturnType(methodParameter);
//            }
//        };
//    }
}