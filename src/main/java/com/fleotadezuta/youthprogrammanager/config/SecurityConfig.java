package com.fleotadezuta.youthprogrammanager.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@EnableWebFluxSecurity
@Configuration
@AllArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {
    //todo mapping auth0 permission

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(expressionInterceptUrlRegistry ->
                        expressionInterceptUrlRegistry
                                .pathMatchers("/payment/**").permitAll()
                                .anyExchange()
                                .authenticated()

                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();
    }
}


