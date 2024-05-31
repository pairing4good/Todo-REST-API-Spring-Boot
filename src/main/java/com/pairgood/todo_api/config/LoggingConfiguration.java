package com.pairgood.todo_api.config;

import org.slf4j.*;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.*;
import org.springframework.core.*;
import java.lang.reflect.Field;
import static java.util.Optional.*;


@SuppressWarnings("unused")
@Configuration
public class LoggingConfiguration {
    @SuppressWarnings("rawtypes")
    @Bean
    @Scope("prototype")
    public Logger logger(final InjectionPoint ip) {
        return LoggerFactory.getLogger(ofNullable(ip.getMethodParameter())
                .<Class>map(MethodParameter::getContainingClass)
                .orElseGet( () ->
                        ofNullable(ip.getField())
                                .map(Field::getDeclaringClass)
                                .orElseThrow (IllegalArgumentException::new)
                )
        );
    }
}

