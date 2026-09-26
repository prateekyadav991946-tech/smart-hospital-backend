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

                        // Own profile
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/me"
                        )
                        .hasRole("PATIENT")

                        // Own medical history
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/patients/me/medical-history"
                        )
                        .hasRole("PATIENT")

                        // -------------------------------------------------
                        // STAFF - PATIENT DATA
                        // -------------------------------------------------

                        // Staff can view patient list/details/history
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

                        // Create patient
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/patients/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "RECEPTIONIST"
                        )

                        // Update patient
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/patients/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "RECEPTIONIST"
                        )

                        // Delete patient
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/patients/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // DOCTORS
                        // =================================================

                        // Everyone relevant can view doctor list
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

                        // Only Admin can create
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        // Only Admin can update
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        // Only Admin can delete
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // APPOINTMENTS
                        // =================================================

                        // Patient can book his/her own appointment
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/appointments/my"
                        )
                        .hasRole("PATIENT")

                        // Hospital staff
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

                        // Doctor/Admin can order lab test
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/lab-tests"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR"
                        )

                        // Admin / Doctor / Lab can view lab information
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/lab-tests/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "LAB_TECHNICIAN"
                        )

                        // Lab processing
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