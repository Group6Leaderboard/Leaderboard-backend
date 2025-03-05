package com.leaderboard.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF (needed for PUT, DELETE, PATCH)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()  // Allow POST
                        .requestMatchers(HttpMethod.PUT, "/api/**").permitAll()   // Allow PUT
                        .requestMatchers(HttpMethod.DELETE, "/api/**").permitAll() // Allow DELETE
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()   // Allow GET
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
