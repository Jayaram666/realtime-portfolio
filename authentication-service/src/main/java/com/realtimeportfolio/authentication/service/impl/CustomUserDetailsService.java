package com.realtimeportfolio.authentication.service.impl;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.entity.User;
import com.realtimeportfolio.authentication.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        log.debug("Loading user details. email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User details lookup failed. email={}", email);
                    return new UsernameNotFoundException("User not found");
                });

        log.debug("User details loaded. userId={}, email={}", user.getId(), user.getEmail());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}