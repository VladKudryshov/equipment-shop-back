package com.sites.equipmentshop.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sites.equipmentshop.security.domain.AccountCredentials;
import com.sites.equipmentshop.security.domain.LoggedUser;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import com.sites.equipmentshop.security.serializers.LoginUserDTOSerializer;
import com.sites.equipmentshop.security.services.TokenAuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	private TokenAuthenticationService authenticationService;
	private JPAUserRepository userRepo;
	private LoginUserDTOSerializer serializer;
	private final ObjectMapper jsonMapper;
	
	public JWTLoginFilter(String url, AuthenticationManager authManager, TokenAuthenticationService authenticationService, JPAUserRepository userRepo) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.authenticationService = authenticationService;
		this.userRepo = userRepo;
		this.serializer = new LoginUserDTOSerializer();
		this.jsonMapper = new ObjectMapper();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws IOException {
		AccountCredentials creds = jsonMapper.readValue(req.getInputStream(), AccountCredentials.class);
		String email = creds.getEmail();
		String pass = creds.getPassword();
		
		if (Objects.isNull(email)) {
			throw new BadCredentialsException("No client credentials presented");
		}

		if (pass == null) {
			pass = StringUtils.EMPTY;
		}

		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(email, pass));
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof BadCredentialsException) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The email and password you entered did not match our records.");
		} else {
			super.unsuccessfulAuthentication(request, response, failed);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		UserEntity entity = userRepo.findUserByEmail(auth.getName());
		entity.setLastLogin(new Date());
		userRepo.save(entity);
		String json = serializer.serialize(new LoggedUser(entity));
		res.setCharacterEncoding("UTF-8");
		res.getWriter().print(json);
		authenticationService.addAccessToken(res, entity);
		authenticationService.addRefreshToken(res, entity);
	}
	
}