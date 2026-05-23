package br.com.security.proposal.infra.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.security.proposal.infra.persistence.entity.ProposalEntity;

public interface ProposalEntityRepository extends JpaRepository<ProposalEntity, UUID> {

    List<ProposalEntity> findAllByOwnerId(UUID ownerId);

}
