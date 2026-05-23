package br.com.security.proposal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.security.proposal.domain.entity.Owner;
import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;
import br.com.security.proposal.domain.entity.ProposalId;

public interface ProposalRepository extends JpaRepository<Proposal, ProposalId> {
    List<Proposal> findByOwner(Owner owner);

    List<Proposal> findAllByOwnerId(OwnerId ownerId);
}
