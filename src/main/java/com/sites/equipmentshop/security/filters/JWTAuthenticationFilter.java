package com.sites.equipmentshop.security.filters;

import com.sites.equipmentshop.security.exceptions.WrongTokenException;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import com.sites.equipmentshop.security.services.TokenAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private final TokenAuthenticationService authenticationService;
    private final JPAUserRepository userRepo;


    public JWTAuthenticationFilter(TokenAuthenticationService authenticationService, JPAUserRepository userRepo) {
        this.authenticationService = authenticationService;
        this.userRepo = userRepo;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            String email = authenticationService.getUserNameFromToken(request);
            UserEntity user = userRepo.findUserByEmail(email);
            Authentication authentication = user != null && user.getEmail().equals(email)
                    ? authenticationService.getAccessAuthentication(request)
                    : null;
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.debug("JWT has been expired");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"message\": \"TOKEN_EXPIRED\"}");
        } catch (SignatureException | MalformedJwtException e) {
            log.debug("JWT has been broken");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JWTRefreshFilter.REFRESH_TOKEN_EXPIRED_MESSAGE);
        } catch (WrongTokenException e) {
            log.debug("JWT has been empty or with wrong type");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JWTRefreshFilter.REFRESH_TOKEN_EXPIRED_MESSAGE);
        }
    }
}
