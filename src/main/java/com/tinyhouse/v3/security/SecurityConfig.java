package com.tinyhouse.v3.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public Endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/house/**").permitAll()

                        // 🔐 General Authenticated Users
                        .requestMatchers("/user/info").authenticated()

                        // 👤 RENTER Role
                        .requestMatchers(HttpMethod.GET, "/house/all").hasRole("RENTER")
                        .requestMatchers(HttpMethod.POST, "/reservation/create").hasRole("RENTER")
                        .requestMatchers(HttpMethod.DELETE, "/reservation/cancel/**").hasRole("RENTER")
                        .requestMatchers(HttpMethod.GET, "/reservation/list").hasRole("RENTER")
                        .requestMatchers(HttpMethod.POST, "/review/create").hasRole("RENTER")
                        .requestMatchers(HttpMethod.POST, "/payment").hasRole("RENTER")

                        // 🏠 OWNER Role
                        .requestMatchers(HttpMethod.POST, "/house/add").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/house/update/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/house/delete/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/house/list").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/reservation/owner-list").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/payment/owner").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/payment/total").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/reservation/approve/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/reservation/cancel/**").hasRole("OWNER")


                        // 🛡️ ADMIN Role
                        .requestMatchers(HttpMethod.GET, "/admin/user/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/user/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/reservation/list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/reservation/cancel/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/reservation/approve/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/house/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/house/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/house/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/payment/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/payment/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/payment/total").hasRole("ADMIN")


                        // 🔐 Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return security.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
