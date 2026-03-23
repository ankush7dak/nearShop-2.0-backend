package com.nearShop.java.security.config;

import com.nearShop.java.security.jwt.JwtFilter;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.modelmapper.ModelMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF for dev
                .cors()
                .and()
                .authorizeHttpRequests(auth -> auth
                        // public endpoints
                        .requestMatchers("/auth/**", "/api/shop/**").permitAll()
                        // role-based endpoints (match lowercase DB roles)
                        .requestMatchers("/shopkeeper/**").hasAuthority("shopkeeper")
                        .requestMatchers("/customer/**").hasAuthority("customer")
                        // all others require auth
                        .anyRequest().authenticated())
                // add JWT filter after public endpoints
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
public S3Client s3Client() {
    return S3Client.builder()
            .endpointOverride(URI.create("https://013976dd37fa395dc9d2f09ec8709cf4.r2.cloudflarestorage.com"))
            .region(Region.US_EAST_1) // ✅ FIXED
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("39fbc477c9d93b6711c3138dbc099857\r\n" + //
                                                                "", "15ec7e12c5273df1d1abbcb3e394df6233a94a5769c904eba964947f067f182d")
                    )
            )
            .build();
}
}
