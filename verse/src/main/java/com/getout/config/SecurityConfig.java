//package com.getout.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .headers(headers -> headers
//                                .contentSecurityPolicy("default-src 'self'; img-src 'self' https://trusted-images.example.com; script-src 'self' https://trusted-scripts.example.com; style-src 'self' 'unsafe-inline';")
//                        // For reportOnly, you might need to adjust based on your Spring Security version or handle it differently.
//                )
//                .authorizeRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                );
//        return http.build();
//    }
//}
