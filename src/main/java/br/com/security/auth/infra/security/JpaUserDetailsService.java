package br.com.security.auth.infra.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.security.auth.infra.persistence.repository.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public JpaUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
