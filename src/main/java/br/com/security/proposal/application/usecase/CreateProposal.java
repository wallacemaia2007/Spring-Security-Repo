package br.com.security.proposal.application.usecase;

import org.springframework.stereotype.Service;

import br.com.security.proposal.application.input.CreateProposalInput;
import br.com.security.proposal.application.output.ProposalOutput;
import br.com.security.proposal.domain.entity.Owner;
import br.com.security.proposal.domain.repository.ProposalRepository;

@Service
public class CreateProposal {

    private final ProposalRepository proposalRepository;

    public CreateProposal(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public ProposalOutput execute(CreateProposalInput input, Owner owner) {
        var proposal = input.toDomain(owner);
        var saved = proposalRepository.save(proposal);
        return ProposalOutput.fromDomain(saved);
    }

}
