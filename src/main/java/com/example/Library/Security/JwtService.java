package com.example.Library.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/*
 * Handles all JWT(JSON Web Token) related operations:
 * generating tokens, validating them and extracting claims like the username or expiration date
 */

@Service
public class JwtService {
    //Secret key is injected from application properties or environment variables
    @Value("${jwt.secret}")
    private String secretKey;


    //Creates a signing key from the secret string using HMAC(Hash-Based Message Authentication code)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //Generates the JWT
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration time
                .signWith(getSigningKey())
                .compact();
    }

    //Validates the JWT
    public boolean isTokenValid(String token, String userEmail) {
        final String extractedEmail = extractUsername(token);
        return extractedEmail.equals(userEmail) && !isTokenExpired(token);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
