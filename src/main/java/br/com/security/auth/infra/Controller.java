package br.com.security.auth.infra;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.security.auth.infra.persistence.entity.User;

@RestController
@RequestMapping
public class Controller {

    @GetMapping("")
    public String getMethodName(@AuthenticationPrincipal User userDetails) {
        return "Hello World " + userDetails.getId();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')") // Libera o acesso apenas para usuários com a role "USER"
    public String getInfluencer() {
        return "Hello World USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Libera o acesso apenas para usuários com a role "ADMIN"
    public String getBrand() {
        return "Hello World ADMIN";
    }

}
