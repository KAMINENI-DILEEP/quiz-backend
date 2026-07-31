package com.exam.config;

import com.exam.security.JwtFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtFilter jwtFilter
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Allow CORS preflight requests
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // Public API endpoints
                .requestMatchers(
                    "/api/login",
                    "/api/register",
                    "/api/forgot-password",
                    "/api/reset-password",
                    "/api/ping"
                ).permitAll()

                /*
                 * JwtFilter creates:
                 *
                 * ROLE_ADMIN
                 * ROLE_STUDENT
                 *
                 * Therefore use hasRole() here.
                 */

                // Student endpoints
                .requestMatchers(
                    "/api/student/**"
                ).hasRole("STUDENT")

                // Admin endpoints
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // Global results - Admin only
                .requestMatchers(
                    "/api/results"
                ).hasRole("ADMIN")

                // Profile endpoints available to logged-in users
                .requestMatchers(
                    "/api/profile/**"
                ).authenticated()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
            Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",

                "http://localhost:5173",
                "http://127.0.0.1:5173",

                "https://*.vercel.app"
            )
        );

        configuration.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS",
                "HEAD"
            )
        );

        configuration.setAllowedHeaders(
            Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept"
            )
        );

        configuration.setExposedHeaders(
            Arrays.asList(
                "Authorization"
            )
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(4);
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}
