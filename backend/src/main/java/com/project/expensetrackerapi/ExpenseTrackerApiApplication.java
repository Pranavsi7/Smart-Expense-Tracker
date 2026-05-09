package com.project.expensetrackerapi;

import com.project.expensetrackerapi.filters.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@SpringBootApplication
public class ExpenseTrackerApiApplication {

    @Autowired
    private AuthFilter authFilter;

    /**
     * ALLOWED_ORIGIN env var:
     *   - Local/Docker  : http://localhost:3000
     *   - Railway prod  : https://your-app.vercel.app
     *   - Default       : * (open, fine for initial deploy)
     */
    @Value("${allowed.origin:*}")
    private String allowedOrigin;

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApiApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        if ("*".equals(allowedOrigin)) {
            config.setAllowedOriginPatterns(List.of("*"));
        } else {
            // Allow both the exact origin and localhost for dev convenience
            config.setAllowedOriginPatterns(List.of(allowedOrigin, "http://localhost:3000", "http://localhost:5173"));
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(authFilter);
        bean.addUrlPatterns("/api/categories/*");
        bean.setOrder(1);
        return bean;
    }
}
