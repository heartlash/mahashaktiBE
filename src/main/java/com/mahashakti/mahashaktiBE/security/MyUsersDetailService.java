package com.mahashakti.mahashaktiBE.security;

import com.mahashakti.mahashaktiBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mahashakti.mahashaktiBE.entities.UserEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUsersDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> user = userRepository.findByEmailAndStatus(username, "ACTIVE");

        return user.map(userEntity -> User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().split(","))
                .build()).orElseThrow();
    }
}
