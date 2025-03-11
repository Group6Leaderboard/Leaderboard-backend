package com.leaderboard.demo.config;

import com.leaderboard.demo.entity.User;
import com.leaderboard.demo.entity.College;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private Key secretKeyDecoded;
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT Secret Key must be at least 32 bytes long");
        }
        this.secretKeyDecoded = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return generateToken(user.getEmail(), user.getRole().getName(), "USER");
    }

    public String generateToken(College college) {
        return generateToken(college.getEmail(), "COLLEGE", "COLLEGE");
    }

    private String generateToken(String email, String role, String entityType) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("type", entityType) // Differentiates USER vs COLLEGE
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKeyDecoded, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String extractType(String token) {
        return getClaims(token).get("type", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
