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
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception{
        security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x ->
                        x.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/user/info").authenticated()
                                .requestMatchers("/reservation/test-auth").authenticated() // Yeni kural
                                .requestMatchers(HttpMethod.POST, "/house/add").hasRole("OWNER")
                                .requestMatchers(HttpMethod.PUT, "/house/update/**").hasRole("OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/house/delete/**").hasRole("OWNER")
                                .requestMatchers(HttpMethod.GET, "/house/list").hasRole("OWNER")
                                .requestMatchers(HttpMethod.POST, "/reservation/create").hasRole("RENTER")
                                .requestMatchers(HttpMethod.DELETE, "/reservation/cancel/**").hasRole("RENTER")
                                .requestMatchers(HttpMethod.GET, "/reservation/list").hasRole("RENTER")
                                .requestMatchers(HttpMethod.GET, "/reservation/owner-list").hasRole("OWNER")
                                .requestMatchers(HttpMethod.POST, "/review/create").hasRole("RENTER")
                                .requestMatchers(HttpMethod.GET, "/review/house/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/payment").hasRole("RENTER")
                                .requestMatchers(HttpMethod.GET, "/payment/owner").hasRole("OWNER")
                                .requestMatchers(HttpMethod.GET, "/payment/total").hasRole("OWNER")



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
