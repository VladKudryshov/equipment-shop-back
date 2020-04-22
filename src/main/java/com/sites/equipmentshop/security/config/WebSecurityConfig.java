package com.sites.equipmentshop.security.config;

import com.sites.equipmentshop.security.filters.JWTAuthenticationFilter;
import com.sites.equipmentshop.security.filters.JWTLoginFilter;
import com.sites.equipmentshop.security.filters.JWTRefreshFilter;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import com.sites.equipmentshop.security.services.PlatformAuthenticationProvider;
import com.sites.equipmentshop.security.services.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_ROLE = "ADMIN";
    private final PlatformAuthenticationProvider authenticationProvider;
    private final TokenAuthenticationService authenticationService;
    private final JPAUserRepository userRepo;
    private final Boolean isEnabled;

    @Autowired
    public WebSecurityConfig(
            PlatformAuthenticationProvider authenticationProvider,
            TokenAuthenticationService authenticationService,
            JPAUserRepository userRepo,
            @Value("${security.enabled}") Boolean isEnabled) {
        this.userRepo = userRepo;
        this.isEnabled = isEnabled;
        this.authenticationService = authenticationService;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(authenticationService, userRepo);
    }

    @Bean
    public JWTRefreshFilter jwtRefreshFilter() {
        return new JWTRefreshFilter("/api/users/token", authenticationService, userRepo);
    }

    @Bean
    public JWTLoginFilter jwtLoginFilter() throws Exception {
        return new JWTLoginFilter("/api/users/login", authenticationManager(), authenticationService, userRepo);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (Boolean.TRUE.equals(isEnabled)) {
            http.csrf().disable().authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/users/token").permitAll()
                    .antMatchers("/api/users/password/**").permitAll()
                    .antMatchers("/api/users/registration").permitAll()
                    .antMatchers("/api/users/**/modify").authenticated()
                    .antMatchers("/api/users/**").hasRole(ADMIN_ROLE)
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtRefreshFilter(), JWTAuthenticationFilter.class)
                    .addFilterBefore(jwtLoginFilter(), JWTRefreshFilter.class);
        } else {
            http.csrf().disable().authorizeRequests()
                    .antMatchers("/**").permitAll();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
