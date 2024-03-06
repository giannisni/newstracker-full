//package com.getout.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//@EnableWebSecurity
//public class AppConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://localhost:5173"); // Allows the frontend origin
//        config.addAllowedOrigin("http://localhost:8082"); // Allows the frontend origin
//        config.addAllowedOrigin("http://localhost:3000"); // Allows the frontend origin
//
//        config.addAllowedMethod("*"); // Allows all methods
//        config.addAllowedHeader("*"); // Allows all headers
//        config.setAllowCredentials(true); // Allows credentials
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // Apply CORS configuration to all paths
//        return new CorsFilter(source);
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors().and()
//                .csrf().disable()
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/**").permitAll() // Use requestMatchers().permitAll() for public endpoints
//                        .anyRequest().authenticated() // Require authentication for all other requests
//                )
//                .httpBasic(); // Example of basic auth, adjust according to your needs
//
//        return http.build();
//    }
//}
