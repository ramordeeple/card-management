package com.pm.bankcards.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwt;
    private final CustomUserDetailsService users;

    public JwtAuthFilter(JwtTokenService jwt, CustomUserDetailsService users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        boolean isPublic =
                path.equals("/api/v1/auth/login") ||
                        path.equals("/api/v1/auth/register") ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs");

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        System.out.println(req.getMethod()+" "+req.getRequestURI()+" | Auth="+req.getHeader("Authorization"));

        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(7);
            try {
                String username = jwt.getUsername(token);
                jwt.validate(token);

                UserDetails user = users.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }



        chain.doFilter(req, res);
    }
}

