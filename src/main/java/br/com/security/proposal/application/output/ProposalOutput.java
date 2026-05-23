package br.com.security.proposal.application.output;

import java.util.Optional;

import br.com.security.proposal.domain.entity.Proposal;

public record ProposalOutput(String title, Optional<String> description, String ownerId, String ownerName) {

    public static ProposalOutput fromDomain(Proposal proposal) {
        return new ProposalOutput(proposal.getTitle(), proposal.getDescription(), proposal.getOwner().id().toString(),
                proposal.getOwner().name());
    }

}
