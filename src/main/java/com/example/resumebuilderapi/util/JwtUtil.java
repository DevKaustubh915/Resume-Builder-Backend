package com.example.resumebuilderapi.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String generateToken(String userId){
        log.info("Inside JwtUtil: generateToken() {}",userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+jwtExpiration);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        log.info("Inside JwtUtil: getSigningKey()");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public String getUserIdFromToken(String token) {
        log.info("Inside JwtUtil: getUserFromToken()");
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token){
        log.info("Inside JwtUtil: validateToken()");
        try{
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token);
            return true;

        }catch (JwtException | IllegalArgumentException e ){
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        log.info("Inside JwtUtil: isTokenExpired()");
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return true;
        }
    }
}
