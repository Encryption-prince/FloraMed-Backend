package com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.TokenBacklistService;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBacklistService tokenBacklistService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService, TokenBacklistService tokenBacklistService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBacklistService = tokenBacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/auth/complete-signup") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/plants/all") ||
                path.startsWith("/plants/filter")||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/home") ||
                path.startsWith("/comments/by-blog/") ||
                path.startsWith("/feedback") ||
               path.startsWith("/marketplace/products/all") ||
               path.startsWith("/test/mail/send")) {
            // Skip public endpoints
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or Invalid Token");
            return;
        }

        String token = authHeader.substring(7);
        // 🔒 Check if token is blacklisted
        if (tokenBacklistService.isTokenBlacklisted(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been logged out");
            return;
        }
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);
        if (email == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Lookup user to get role
            User user = userService.findByEmail(email)
                    .orElse(null);

            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            if (user.isBanned()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account banned");
                return;
            }

            // Build authorities list, e.g. ROLE_HERBALIST or ROLE_USER
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
