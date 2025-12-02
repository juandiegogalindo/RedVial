package com.redvial.config;

import com.redvial.security.JwtAuthenticationFilter;
import com.redvial.security.JwtUtil;
import com.redvial.security.UserDetailsServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // ============================
    // JWT FILTER
    // ============================
    @Bean
    public JwtAuthenticationFilter jwtAuthFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    // ============================
    // AUTH PROVIDER
    // ============================
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ============================
    // CORS
    // ============================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ============================
    // SECURITY FILTER CHAIN
    // ============================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ======================================
                //  PERMITIDOS SIN TOKEN (LOGIN / REGISTRO)
                // ======================================
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/registro/**").permitAll()

                // =================================================
                //  🔥 CAMBIO IMPORTANTE:
                //  GET de ofertas → público (para que cargue listado)
                // =================================================
                .requestMatchers(HttpMethod.GET, "/api/ofertas/**").permitAll()

                // =================================================
                //  El resto de métodos de ofertas → requieren login
                //  (POST, DELETE, aceptar, etc.)
                // =================================================
                .requestMatchers("/api/ofertas/**").authenticated()

                // =================================================
                // CONTACTO → requiere estar logueado (como antes)
                // =================================================
                .requestMatchers("/api/contacto/**").authenticated()

                // ======================================
                //  TODO LO DEMÁS → PERMITIDO (HTML, CSS, JS)
                // ======================================
                .anyRequest().permitAll()
            )
            .authenticationProvider(authProvider())

            // Filtro JWT antes que el UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ============================
    // PASSWORD ENCODER
    // ============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================
    // AUTH MANAGER
    // ============================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
