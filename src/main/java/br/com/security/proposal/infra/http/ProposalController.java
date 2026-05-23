package br.com.security.proposal.infra.http;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.security.auth.domain.UserRole;
import br.com.security.auth.infra.persistence.entity.User;
import br.com.security.proposal.application.enums.AcessScope;
import br.com.security.proposal.application.usecase.CreateProposalUseCase;
import br.com.security.proposal.application.usecase.ListProposalUseCase;
import br.com.security.proposal.domain.entity.Owner;
import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.infra.http.request.CreateProposalRequest;
import br.com.security.proposal.infra.http.response.ProposalResponse;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final CreateProposalUseCase createProposalUseCase;
    private final ListProposalUseCase listProposalUseCase;

    public ProposalController(CreateProposalUseCase createProposalUseCase, ListProposalUseCase listProposalUseCase) {
        this.createProposalUseCase = createProposalUseCase;
        this.listProposalUseCase = listProposalUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProposalResponse> creatProposal(@RequestBody CreateProposalRequest request,
            @AuthenticationPrincipal User user) {

        var ower = new Owner(new OwnerId(user.getId()), user.getUsername());

        var output = this.createProposalUseCase.execute(request.toInput(), ower);

        return ResponseEntity.ok().body(ProposalResponse.from(output));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public ResponseEntity<List<ProposalResponse>> findAllProposals(@AuthenticationPrincipal User user) {
        var acessScope = acessScope(user.getRole());
        var owerId = new OwnerId(user.getId());
        var response = listProposalUseCase.execute(acessScope, owerId).stream().map(ProposalResponse::from).toList();

        return ResponseEntity.ok().body(response);

    }

    private static AcessScope acessScope(UserRole userRole) {
        return userRole == UserRole.ROLE_ADMIN ? AcessScope.ALL : AcessScope.OWN;
    }

}
