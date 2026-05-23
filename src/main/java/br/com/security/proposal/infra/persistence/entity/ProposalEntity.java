package br.com.security.proposal.infra.persistence.entity;

import java.util.Optional;
import java.util.UUID;

import br.com.security.proposal.domain.entity.Owner;
import br.com.security.proposal.domain.entity.OwnerId;
import br.com.security.proposal.domain.entity.Proposal;
import br.com.security.proposal.domain.entity.ProposalId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String ownerName;

    public static ProposalEntity from(Proposal proposal) {
        ProposalEntity proposalEntity = new ProposalEntity(proposal.getId().id(), proposal.getTitle(),
                proposal.getDescription().orElse(null),
                proposal.getOwner().id().id(), proposal.getOwner().name());
        return proposalEntity;
    }

    public Proposal toDomain() {
        return new Proposal(new ProposalId(this.id), this.title, Optional.ofNullable(this.description),
                new Owner(new OwnerId(this.ownerId), this.ownerName));
    }

}
