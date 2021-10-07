package br.com.springboot.service;

import br.com.springboot.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemUserDetailsService implements UserDetailsService {

    private final SystemUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("SystemUser not found"));
    }

}
