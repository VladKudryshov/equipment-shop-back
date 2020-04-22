package com.sites.equipmentshop.security.services;

import com.sites.equipmentshop.security.UserService;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlatformAuthenticationProvider implements AuthenticationProvider {

    private final PlatformUserDetailsService platformUserDetailsService;
    private final JPAUserRepository userRepository;

    private static final String BAD_CREDENTIALS_MESSAGE = "Authentication error: user name or password is wrong";
    private static final String USER_ENTITY_NOT_FOUND = "User not found in database";
    private static final int MD5_HASH_LENGTH = 32;
    private final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

    @Autowired
    public PlatformAuthenticationProvider(PlatformUserDetailsService platformUserDetailsService,
                                          JPAUserRepository userRepository) {
        this.platformUserDetailsService = platformUserDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String userEmail = authentication.getName();
        String userPassword = authentication.getCredentials().toString();

        UserDetails user = platformUserDetailsService.loadUserByUsername(userEmail);

        if (Objects.nonNull(user)) {
            if (StringUtils.length(user.getPassword()) == MD5_HASH_LENGTH) {
                return oldAuth(user, userPassword);
            } else {
                if (bCrypt.matches(userPassword, user.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
                } else {
                    throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
                }
            }
        } else {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }
    }

    private UsernamePasswordAuthenticationToken oldAuth(UserDetails user, String userPassword) {
        if (StringUtils.equals(UserService.md5Hex(userPassword), user.getPassword())) {
            String newHashPassword = bCrypt.encode(userPassword);
            UserEntity userEntity = userRepository.findUserByEmail(user.getUsername());
            if (Objects.nonNull(userEntity)) {
                userEntity.setPassword(newHashPassword);
                userRepository.save(userEntity);
                return new UsernamePasswordAuthenticationToken(user.getUsername(), newHashPassword);
            } else {
                throw new BadCredentialsException(USER_ENTITY_NOT_FOUND);
            }
        } else {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
