package br.com.security.proposal.application.input;

import java.util.Optional;

import br.com.security.proposal.domain.entity.Owner;
import br.com.security.proposal.domain.entity.Proposal;

public record CreateProposalInput(String title, Optional<String> description) {

    public Proposal toDomain(Owner owner) {
        return new Proposal(title, description, owner);
    }

}
