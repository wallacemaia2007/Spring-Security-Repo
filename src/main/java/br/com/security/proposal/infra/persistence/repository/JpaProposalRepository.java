package br.com.security.proposal.infra.persistence.repository;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Repository;

import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;
import br.com.security.proposal.domain.repository.ProposalRepository;
import br.com.security.proposal.infra.persistence.entity.ProposalEntity;

@Repository
public class JpaProposalRepository implements ProposalRepository {

    private final ProposalEntityRepository proposalEntityRepository;

    public JpaProposalRepository(ProposalEntityRepository proposalEntityRepository) {
        this.proposalEntityRepository = proposalEntityRepository;
    }

    @Override
    public List<Proposal> findAllByOwnerId(OwnerId id) {
        return proposalEntityRepository.findAllByOwnerId(id.id()).stream().map(ProposalEntity::toDomain).toList();

    }

    @Override
    public List<Proposal> findAll() {
        var iterable = proposalEntityRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(ProposalEntity::toDomain)
                .toList();
    }

    @Override
    public Proposal save(Proposal proposal) {

        var pro = ProposalEntity.from(proposal);
        var saved = proposalEntityRepository.save(pro);
        return saved.toDomain();

    }

}
