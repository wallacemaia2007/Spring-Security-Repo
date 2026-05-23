package br.com.security.proposal.infra.http.request;

import java.util.Optional;

import br.com.security.proposal.application.input.CreateProposalInput;

public record CreateProposalRequest(String title, Optional<String> description) {

    public CreateProposalInput toInput() {
        return new CreateProposalInput(title, description);
    }

}
