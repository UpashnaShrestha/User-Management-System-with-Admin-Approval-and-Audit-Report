package com.project.UserManagementSystem.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret_key;
    @Value("${jwt.access_token.expiration}")
    private long accessTokenExpiration;
    @Getter
    @Value("${jwt.refresh_token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret_key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> claims, long expirationDuration, String tokenType) {

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Date now = new Date();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .claim("roles", roles)
                .claim("token_type", tokenType)
                .claims(claims)
                .expiration(new Date(now.getTime() + expirationDuration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(UserDetails userDetails, Map<String, Object> claims) {
        return generateToken(userDetails, claims, accessTokenExpiration, "access_token");
    }

    public String generateRefreshToken(UserDetails userDetails, Map<String, Object> claims) {
        return generateToken(userDetails, claims,  refreshTokenExpiration,"refresh_token");
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }


    public Boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    public String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(bytes);

        }
        catch (Exception e){
            throw new IllegalStateException("Could not hash token",e);

        }
    }

    //rejects access token if someone sends refreshToken
    public String extractTokenType(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("token_type").toString();
    }
}
