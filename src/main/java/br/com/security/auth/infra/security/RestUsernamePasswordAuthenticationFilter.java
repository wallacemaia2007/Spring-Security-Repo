package br.com.security.auth.infra.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class RestUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public RestUsernamePasswordAuthenticationFilter(AuthenticationConfiguration configuration,
            ObjectMapper objectMapper) {

        super(configuration.getAuthenticationManager());

        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/auth/login"); // Define a URL personalizada para o endpoint de login
        setAuthenticationSuccessHandler(
                (request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    request.getSession(true);
                });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            var token = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

            return getAuthenticationManager().authenticate(token);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Unable to parse authentication request body", e);
        }

    }

    public record LoginRequest(String username, String password) {
    }

}
