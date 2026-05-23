package br.com.security.proposal.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.security.proposal.application.Factory;
import br.com.security.proposal.application.enums.AcessScope;
import br.com.security.proposal.application.output.ProposalOutput;
import br.com.security.proposal.domain.entity.OwnerId;

@Service
public class ListProposalUseCase {

    private final Factory factory;

    public ListProposalUseCase(Factory factory) {
        this.factory = factory;
    }

    public List<ProposalOutput> execute(AcessScope scope, OwnerId id) {
        var proposals = factory.getStrategy(scope).getProposals(id);

        return proposals.stream().map(ProposalOutput::fromDomain)
                .collect(Collectors.toList());
    }

}
