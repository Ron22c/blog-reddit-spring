package com.cranajit.Blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtProvider {

    private final String SECRET_KEY = "UserManageMentSystem@1234$%";

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createJwtToken(claims, userDetails.getUsername());
    }

    public String createJwtToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() * 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public <T>T getClaims(String token, Function<Claims, T> claimSource) {
        Claims claims = getAllClaims(token);
        return claimSource.apply(claims);
    }

    public String extractUsername(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public Date extractExpireTime(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    public Boolean isExpired(String token) {
        return extractExpireTime(token).before(new Date());
    }

    public Boolean validate(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUsername(token)) && !isExpired(token);
    }
}
