package com.elasticai.assistant.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.elasticai.assistant.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Skip JWT validation for login/register
        String path = request.getServletPath();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization Header: " + authHeader);

        // 3. No token → continue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extract token
        String token = authHeader.substring(7).trim();

        System.out.println("Token received");

        try {

            // 5. Extract email from JWT
            String email = jwtUtil.extractEmail(token);

            System.out.println("Email: " + email);

            // 6. Check authentication
            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // 7. Load user
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                System.out.println(
                        "User Found: " + userDetails.getUsername());

                // 8. Validate token
                if (jwtUtil.validateToken(
                        token,
                        userDetails.getUsername())) {

                    // 9. Create authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    // 10. Store authentication
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "Authentication Stored");
                }
            }

        } catch (Exception e) {

            // Expired/invalid JWT should NOT crash the application
            System.out.println(
                    "JWT validation failed: " + e.getMessage());

            // Clear authentication
            SecurityContextHolder.clearContext();
        }

        // 11. Continue request
        filterChain.doFilter(request, response);
    }
}