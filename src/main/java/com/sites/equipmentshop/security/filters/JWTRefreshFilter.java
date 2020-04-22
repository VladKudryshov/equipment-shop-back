package com.sites.equipmentshop.security.filters;

import com.sites.equipmentshop.security.exceptions.UserNotExistsException;
import com.sites.equipmentshop.security.exceptions.WrongTokenException;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import com.sites.equipmentshop.security.services.TokenAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTRefreshFilter extends GenericFilterBean {

    private TokenAuthenticationService authenticationService;
    private JPAUserRepository userRepo;
    private RequestMatcher requestMatcher;

    public static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "{\"message\": \"REFRESH_TOKEN_EXPIRED\"}";

    private static final Logger log = LoggerFactory.getLogger(JWTRefreshFilter.class);

    public JWTRefreshFilter(String url, TokenAuthenticationService authenticationService, JPAUserRepository userRepo) {
        this.authenticationService = authenticationService;
        this.userRepo = userRepo;
        requestMatcher = new AntPathRequestMatcher(url, HttpMethod.GET.toString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (requestMatcher.matches((HttpServletRequest) request)) {
            try {
                Authentication authentication = authenticationService.getRefreshAuthentication(request);
                UserEntity entity = userRepo.findById(authentication.getPrincipal().toString())
                        .orElseThrow(() -> new UserNotExistsException(String.format("User [%s] doesn't exist", authentication.getName())));
                authenticationService.addAccessToken((HttpServletResponse) response, entity);
            } catch (ExpiredJwtException e) {
                log.debug("JWT has been expired");
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(REFRESH_TOKEN_EXPIRED_MESSAGE);
            } catch (SignatureException | MalformedJwtException e) {
                log.debug("JWT has been broken");
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(REFRESH_TOKEN_EXPIRED_MESSAGE);
            } catch (WrongTokenException e) {
                log.debug("JWT has been empty or with wrong type");
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(REFRESH_TOKEN_EXPIRED_MESSAGE);
            } catch (UserNotExistsException e) {
                log.debug(e.getMessage());
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(REFRESH_TOKEN_EXPIRED_MESSAGE);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

}
