package com.sites.equipmentshop.security.services;

import com.google.common.collect.Sets;
import com.sites.equipmentshop.security.Tokens;
import com.sites.equipmentshop.security.UserService;
import com.sites.equipmentshop.security.config.TokenConfig;
import com.sites.equipmentshop.security.exceptions.UserNotExistsException;
import com.sites.equipmentshop.security.exceptions.WrongTokenException;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

@Service
public class TokenAuthenticationService {

    private TokenConfig tokenConfig;

    private UserService userService;

    @Autowired
    public TokenAuthenticationService(UserService userService) {
        this.userService = userService;
    }


    public void addAccessToken(HttpServletResponse res, UserEntity entity) {
        res.addHeader(Tokens.ACCESS_TOKEN.getHeader(),
                buildHeader(entity, tokenConfig.getAccessTokenExpirationTime(), Tokens.ACCESS_TOKEN.getType()));
    }

    public void addRefreshToken(HttpServletResponse res, UserEntity entity) {
        res.addHeader(Tokens.REFRESH_TOKEN.getHeader(),
                buildHeader(entity, tokenConfig.getRefreshTokenExpirationTime(), Tokens.REFRESH_TOKEN.getType()));
    }

    public Authentication getAccessAuthentication(ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader(Tokens.ACCESS_TOKEN.getHeader());
        return parseToken(token, Tokens.ACCESS_TOKEN.getType());
    }

    public Authentication getRefreshAuthentication(ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader(Tokens.REFRESH_TOKEN.getHeader());
        Authentication auth = parseToken(token, Tokens.REFRESH_TOKEN.getType());
        if (auth == null) {
            throw new WrongTokenException();
        }
        return auth;
    }

    public String getUserNameFromToken(final ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader(Tokens.ACCESS_TOKEN.getHeader());
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return getClaims(token).getBody().getSubject();
    }

    private Authentication parseToken(String token, String type) {
        if (token != null && !token.trim().isEmpty()) {
            Jws<Claims> claim = getClaims(token);
            String userId = claim.getBody().getId();

            String role = claim.getHeader().get("role").toString();
            String tokenType = claim.getHeader().get("type").toString();
            return getAuthentication(type, userId, role, tokenType);
        }
        return null;
    }

    public Authentication getAuthentication(String type, String userId, String role, String tokenType) {
        try{
            UserEntity userEntity = userService.getUserById(userId);
            if (Objects.isNull(userEntity) || UserStatuses.getInActive().contains(userEntity.getUserStatus())){
                return null;
            }

        } catch (UserNotExistsException ex){
            return null;
        }
        Collection<GrantedAuthority> authorities = Sets.newHashSet();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return userId != null && tokenType.equals(type) ? new UsernamePasswordAuthenticationToken(userId, null, authorities) : null;
    }

    @Autowired
    public void setTokenConfig(final TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    private Jws<Claims> getClaims(final String token) {
        return Jwts.parser()
                .setSigningKey(tokenConfig.getSecret())
                .parseClaimsJws(token.replace(TokenConfig.TOKEN_PREFIX, StringUtils.EMPTY));
    }

    private String buildHeader(UserEntity entity, long expirationTime, String token) {
        return TokenConfig.TOKEN_PREFIX +
                StringUtils.SPACE +
                tokenConfig.buildToken(entity, expirationTime, token);
    }
}
