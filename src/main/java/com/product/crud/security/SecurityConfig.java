package com.product.crud.security;

import com.product.crud.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CrudService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authz) -> authz.requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/healthz", "GET")).permitAll()

//                .requestMatchers(new AntPathRequestMatcher("/v1/product", "POST")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v1/product/{productId}", "GET")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/v1/product/{productId}", "DELETE")).permitAll()

                .anyRequest()
                .authenticated());
        http.csrf((csrf) -> csrf.disable());
        http.httpBasic();
        return http.build();
    }
}
