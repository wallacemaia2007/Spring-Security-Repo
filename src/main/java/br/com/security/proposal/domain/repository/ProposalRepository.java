package br.com.security.proposal.domain.repository;

import java.util.List;

import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;

public interface ProposalRepository {

    List<Proposal> findAllByOwnerId(OwnerId id);

    List<Proposal> findAll();

    Proposal save(Proposal proposal);

}
