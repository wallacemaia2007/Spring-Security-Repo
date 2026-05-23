package br.com.security.proposal.application;

import java.util.List;

import br.com.security.proposal.application.enums.AcessScope;
import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;

public interface Strategy {

    List<Proposal> getProposals(OwnerId id);

    AcessScope getAcessScope();

}
