package com.eventra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins from configuration
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        origins.forEach(origin -> {
            if (origin.trim().equals("*")) {
                configuration.addAllowedOriginPattern("*");
            } else {
                configuration.addAllowedOrigin(origin.trim());
            }
        });
        
        // Parse allowed methods
        Arrays.asList(allowedMethods.split(",")).forEach(method -> 
            configuration.addAllowedMethod(method.trim()));
        
        // Parse allowed headers
        Arrays.asList(allowedHeaders.split(",")).forEach(header -> 
            configuration.addAllowedHeader(header.trim()));
        
        configuration.setAllowCredentials(allowCredentials);
        
        // Expose headers that frontend might need
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
