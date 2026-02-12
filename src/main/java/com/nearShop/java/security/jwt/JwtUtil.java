package com.nearShop.java.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key (should be kept safe, ideally in environment variables)
    private final String SECRET = "NearShopSuperSecretKeyNearShopSuperSecretKey123";
    
    // Token expiration time: 10 hours
    private final long EXPIRATION = 1000 * 60 * 60 * 10;

    // Convert secret string to Key object
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Generate JWT token with mobile number and role
     * Role is prefixed with ROLE_ for Spring Security hasRole checks
     */
    public String generateToken(String mobile, String role) {
        String roleWithPrefix = role.toLowerCase(); // e.g., ROLE_SHOPKEEPER

        return Jwts.builder()
                .setSubject(mobile)
                .claim("role", roleWithPrefix)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract all claims from the token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract mobile number (subject) from token
     */
    public String extractMobile(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validate token (checks signature and expiration)
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
