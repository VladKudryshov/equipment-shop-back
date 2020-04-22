package com.sites.equipmentshop.security.config;

import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenConfig {
    public static final String TOKEN_PREFIX = "Bearer";

    @Value("${equipments.security.accesstoken.expirationtime}")
    private long accessTokenExpirationTime;
    @Value("${equipments.security.refreshtoken.expirationtime}")
    private long refreshTokenExpirationTime;
    @Value("${equipments.security.secret}")
    private String secret;

    public String buildToken(UserEntity entity, long expirationTime, String type) {
        return Jwts.builder().setId(entity.getId())
                .setSubject(entity.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setHeaderParam("role", entity.getUserRole().toString())
                .setHeaderParam("type", type)
                .signWith(SignatureAlgorithm.HS512, getSecret()).compact();
    }

    public String buildToken(UserEntity entity, String type) {
        return Jwts.builder().setId(entity.getId())
                .setSubject(entity.getEmail())
                .setHeaderParam("role", entity.getUserRole().toString())
                .setHeaderParam("type", type)
                .signWith(SignatureAlgorithm.HS512, getSecret()).compact();
    }

    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }

    public String getSecret() {
        return secret;
    }
}
