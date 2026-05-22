package br.com.security.auth.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                .securityContext(context -> context.requireExplicitSave(false))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(auth, UsernamePasswordAuthenticationFilter.class);

        // .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // Configura um serviço de detalhes do usuário em memória, criando dois usuários
    // com diferentes roles e senhas codificadas
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user").password(encoder.encode("senha")).roles("USER").build();
        UserDetails user2 = User.withUsername("user2").password(encoder.encode("senha2")).roles("ADMIN").build();

        return new InMemoryUserDetailsManager(user, user2);
    }

    // Configura um codificador de senhas usando o algoritmo BCrypt, que é
    // recomendado para armazenar senhas de forma segura
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
