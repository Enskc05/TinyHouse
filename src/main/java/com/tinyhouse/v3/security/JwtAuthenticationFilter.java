package com.tinyhouse.v3.security;

import com.tinyhouse.v3.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Public endpoint kontrolü
        if (authHeader == null && shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token format kontrolü
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid auth header");
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        // Temel validasyonlar
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
    private String validateAuthHeader(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            sendError(response, "Missing or invalid Authorization header");
            return null;
        }
        return authHeader;
    }

    private String extractJwtToken(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private boolean authenticateUser(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String jwt) throws IOException {
        try {
            // 1. Kullanıcı adını çıkar
            String username = jwtService.extractUsername(jwt);
            if (username == null) {
                sendError(response, "Invalid token: No username found");
                return false;
            }

            // 2. SecurityContext kontrolü
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                return true; // Zaten doğrulanmış
            }

            // 3. UserDetails yükle
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                sendError(response, "User not found");
                return false;
            }

            // 4. Token geçerlilik kontrolü
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                sendError(response, "Invalid token");
                return false;
            }

            // 5. Authentication ayarla
            setAuthentication(request, userDetails);
            return true;

        } catch (Exception e) {
            sendError(response, "Authentication failed: " + e.getMessage());
            return false;
        }
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        new ArrayList<>(userDetails.getAuthorities()) // Mutable copy
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.equals("/error");
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message)
        );
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e)
            throws IOException {
        logger.error("Authentication error", e);
        sendError(response, "Internal authentication error");
    }
}