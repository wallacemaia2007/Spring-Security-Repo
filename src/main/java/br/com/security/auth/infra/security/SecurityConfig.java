package br.com.security.auth.infra.security;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.security.auth.domain.UserRole;
import br.com.security.auth.infra.persistence.entity.User;
import br.com.security.auth.infra.persistence.repository.UserRepository;

/*
CSFR -> Cross-Site Request Forgery (CSRF)
*/

@Configuration // Indica que esta classe é uma classe de configuração do Spring
@EnableWebSecurity // Habilita a segurança web do Spring Security
@EnableMethodSecurity // Habilita a segurança baseada em métodos, permitindo o uso de anotações como
                      // @PreAuthorize
public class SecurityConfig {

    // Configura a cadeia de filtros de segurança, definindo as regras de
    // autorização e autenticação
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, RestUsernamePasswordAuthenticationFilter auth) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .securityContext(context -> context.requireExplicitSave(false))
                .authorizeHttpRequests(
                        authorize -> authorize.requestMatchers("/api/auth/login", "/h2-console/**").permitAll()
                                .anyRequest().authenticated())

                .addFilterAt(auth, UsernamePasswordAuthenticationFilter.class);

        // .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // Configura um codificador de senhas usando o algoritmo BCrypt, que é
    // recomendado para armazenar senhas de forma segura
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserRepository repository, PasswordEncoder encoder) {

        return args -> {
            if (repository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setRole(UserRole.ROLE_ADMIN);

                User user = new User();
                user.setUsername("user");
                user.setPassword(encoder.encode("user"));
                user.setRole(UserRole.ROLE_USER);
                repository.saveAll(List.of(admin, user));
            }
        };
    }
}
