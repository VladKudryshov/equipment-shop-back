package com.sites.equipmentshop.security.services;

import com.sites.equipmentshop.security.UserService;
import com.sites.equipmentshop.security.exceptions.UserNotExistsException;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlatformUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public PlatformUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        try {
            UserEntity entity = userService.getActiveUserByEmail(userName);
            return User.builder()
                    .username(entity.getEmail())
                    .password(entity.getPassword())
                    .roles(entity.getUserRole().name())
                    .build();
        } catch (UserNotExistsException e) {
            throw new UsernameNotFoundException(userName);
        }
    }

}
