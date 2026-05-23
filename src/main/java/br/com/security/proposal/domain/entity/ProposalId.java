package br.com.security.proposal.domain.entity;

import java.util.UUID;

public record ProposalId(UUID id) {

    public ProposalId() {
        this(UUID.randomUUID());
    }

}
