package edu.cit.velasco.paystream.service;

import edu.cit.velasco.paystream.entity.User;
import edu.cit.velasco.paystream.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        /**
         * FIX: Spring Security's internal User constructor (org.springframework.security.core.userdetails.User)
         * strictly forbids null or empty password strings. 
         * * Since users logging in via GitHub do not have a password_hash in our database, 
         * we provide a dummy placeholder string here. This satisfies the constructor 
         * and allows the JWT generation process to proceed.
         */
        String passwordForSpring = user.getPasswordHash();
        if (passwordForSpring == null) {
            passwordForSpring = "OAUTH2_USER"; 
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                passwordForSpring,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}