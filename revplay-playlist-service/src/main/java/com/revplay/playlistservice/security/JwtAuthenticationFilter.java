package com.revplay.playlistservice.security;

import com.revplay.playlistservice.service.JwtService;
import com.revplay.playlistservice.service.TokenRevocationService;
import com.revplay.playlistservice.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;

    public JwtAuthenticationFilter(JwtService jwtService, TokenRevocationService tokenRevocationService) {
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            log.info("Playlist-service: No JWT token found or invalid format in Authorization header.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (tokenRevocationService.isRevoked(token)) {
            log.info("Playlist-service: JWT token is revoked: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isAccessToken(token)) {
            log.info("Playlist-service: JWT token is not an access token.");
            filterChain.doFilter(request, response);
            return;
        }

        AuthenticatedUserPrincipal principal = jwtService.toPrincipal(token);
        log.info("Playlist-service: JWT Claims extracted: userId={}, username={}, role={}",
                principal.userId(), principal.username(), principal.role());

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + principal.role().name()),
                new SimpleGrantedAuthority("ROLE_" + UserRole.LISTENER.name())
        );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
