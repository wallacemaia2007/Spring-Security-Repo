package br.com.security.auth.infra;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {

    @GetMapping("")
    public String getMethodName(@AuthenticationPrincipal UserDetails userDetails) {
        return "Hello World " + userDetails.getUsername();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String getInfluencer() {
        return "Hello World USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getBrand() {
        return "Hello World ADMIN";
    }

}
