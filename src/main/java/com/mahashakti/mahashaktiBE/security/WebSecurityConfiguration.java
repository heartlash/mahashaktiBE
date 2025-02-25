package com.mahashakti.mahashaktiBE.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final MyUsersDetailService myUsersDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/users/**", "/error").permitAll();
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/sale/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.POST,"/production/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.PUT,"/production/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.DELETE,"/production/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.GET,"/production/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR", "DOCTOR");
                    registry.requestMatchers("/data/vendors").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.POST,"/flock/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.PUT,"/flock/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.DELETE,"/flock/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR");
                    registry.requestMatchers(HttpMethod.GET,"/flock/**").hasAnyRole("ADMIN", "OWNER", "SUPERVISOR", "DOCTOR");

                    registry.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return myUsersDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUsersDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
}
