package com.smarthospital.prateek.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =====================================================
                // CORS
                // =====================================================

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf.disable())

                // =====================================================
                // SESSION
                // JWT based application -> stateless
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------------------
                        // CORS PREFLIGHT
                        // -------------------------------------------------

                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // -------------------------------------------------
                        // PUBLIC
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/auth/**",
                                "/h2-console/**",
                                "/error"
                        ).permitAll()

                        // -------------------------------------------------
                        // ADMIN
                        // -------------------------------------------------

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // =================================================
                        // PATIENT - OWN DATA ONLY
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/me"
                        )
                        .hasRole("PATIENT")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/me/medical-history"
                        )
                        .hasRole("PATIENT")

                        // -------------------------------------------------
                        // STAFF - PATIENT DATA
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/*/medical-history"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "RECEPTIONIST"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "RECEPTIONIST"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/patients/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "RECEPTIONIST"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/patients/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "RECEPTIONIST"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/patients/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // DOCTORS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/doctors/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "RECEPTIONIST",
                                "PATIENT"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // APPOINTMENTS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/appointments/my"
                        )
                        .hasRole("PATIENT")

                        .requestMatchers(
                                "/api/appointments/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "RECEPTIONIST"
                        )

                        // =================================================
                        // SMART QUEUE
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/queue/my/check-in"
                        )
                        .hasRole("PATIENT")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/queue/my"
                        )
                        .hasRole("PATIENT")

                        .requestMatchers(
                                "/api/queue/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "RECEPTIONIST"
                        )

                        // =================================================
                        // CONSULTATION
                        // =================================================

                        .requestMatchers(
                                "/api/consultations/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR"
                        )

                        // =================================================
                        // PRESCRIPTION
                        // =================================================

                        .requestMatchers(
                                "/api/prescriptions/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR"
                        )

                        // =================================================
                        // LAB
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/lab-tests"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/lab-tests/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "LAB_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/lab-tests/*/start",
                                "/api/lab-tests/*/complete"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "LAB_TECHNICIAN"
                        )

                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )

                // =====================================================
                // H2 CONSOLE
                // =====================================================

                .headers(headers ->
                        headers.frameOptions(
                                frame -> frame.sameOrigin()
                        )
                )

                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                List.of("*")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}