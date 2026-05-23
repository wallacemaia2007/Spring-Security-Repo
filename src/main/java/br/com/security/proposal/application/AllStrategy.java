package br.com.security.proposal.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.security.proposal.application.enums.AcessScope;
import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;
import br.com.security.proposal.domain.repository.ProposalRepository;

@Service
public class AllStrategy implements Strategy {

    private final ProposalRepository proposalRepository;

    public AllStrategy(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Override
    public List<Proposal> getProposals(
            OwnerId id) {
        return proposalRepository.findAll();
    }

    @Override
    public AcessScope getAcessScope() {
        return AcessScope.ALL;
    }

}
