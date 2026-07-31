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

    private static final String SECRET_KEY_STRING =
            "engine_signing_token_secret_key_2026_java_edition";

    private static final long TOKEN_VALIDITY =
            8 * 60 * 60 * 1000L; // 8 hours

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8)
            );

    public String generateToken(String subject, String role) {

        String normalizedRole = role;

        if (normalizedRole != null) {
            normalizedRole = normalizedRole
                    .replace("ROLE_", "")
                    .toUpperCase();
        }

        return Jwts.builder()
                .subject(subject)
                .claim("role", normalizedRole)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + TOKEN_VALIDITY
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
                claims -> claims.get(
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
            Function<Claims, T> claimsResolver
    ) {

        final Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {

        Date expiration =
                extractExpiration(token);

        return expiration == null ||
                expiration.before(new Date());
    }

    public boolean validateToken(
            String token,
            String username
    ) {

        try {

            if (token == null ||
                    username == null ||
                    username.isBlank()) {
                return false;
            }

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
