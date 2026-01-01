package com.project.supplychain.filters;

import com.project.supplychain.JWT.JWT;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.models.user.Admin;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWT jwt;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JWT jwt, UserRepository userRepository) {
        this.jwt = jwt;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader;

            while (token.trim().startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }

            Claims claims = jwt.extractAllClaims(token);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (email == null || role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            User user = userRepository.findByEmail(email);

            if (user == null || !user.isActive()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );
            System.out.println("Email = " + email);
            System.out.println("Role = " + role);
            System.out.println("User from DB = " + user);
            System.out.println("Authorities = " + authorities);
            System.out.println("Authentication SET");
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "Invalid or expired token"
                }
            """);

            return;
        }
        filterChain.doFilter(request, response);
    }
}
