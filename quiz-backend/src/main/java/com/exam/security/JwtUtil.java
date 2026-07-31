package com.exam.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY =
            "engine_signing_token_secret_key_2026_java_edition";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8)
            );

    public String generateToken(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 8 * 60 * 60 * 1000
                        )
                )
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public String extractRole(String token) {

        return extractClaim(
                token,
                claims ->
                        claims.get(
                                "role",
                                String.class
                        )
        );
    }

    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(new Date());
    }

    public boolean validateToken(
            String token,
            String username
    ) {

        try {

            String extractedUsername =
                    extractUsername(token);

            return extractedUsername != null
                    && extractedUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }
}
