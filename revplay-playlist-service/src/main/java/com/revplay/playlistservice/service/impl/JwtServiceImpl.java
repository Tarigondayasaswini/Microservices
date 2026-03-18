package com.revplay.playlistservice.service.impl;

import com.revplay.playlistservice.service.JwtService;
import com.revplay.playlistservice.security.AuthenticatedUserPrincipal;
import com.revplay.playlistservice.security.JwtProperties;
import com.revplay.playlistservice.enums.UserRole;
import com.revplay.playlistservice.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            LOGGER.debug("JWT parsing failed");
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    @Override
    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(parseToken(token).get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Override
    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(parseToken(token).get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Override
    public AuthenticatedUserPrincipal toPrincipal(String token) {
        Claims claims = parseToken(token);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        UserRole role;
        try {
            role = UserRole.from(claims.get(ROLE_CLAIM, String.class));
        } catch (RuntimeException exception) {
            throw new UnauthorizedException("Invalid token role claim");
        }
        return new AuthenticatedUserPrincipal(userId, username, role);
    }

    @Override
    public Instant getExpiry(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration == null ? null : expiration.toInstant();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
