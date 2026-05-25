package com.corestack.backend.security;

import com.corestack.backend.auth.repository.UserRepository;
import com.corestack.backend.common.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID userId;
        try {
            userId = UUID.fromString(username);
        } catch (IllegalArgumentException ex) {
            throw new UsernameNotFoundException("Invalid user id");
        }
        return userRepository.findById(userId)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetails loadUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
