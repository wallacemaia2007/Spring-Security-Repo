package br.com.security.proposal.domain.entity;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Proposal {

    private ProposalId id;
    private String title;
    private Optional<String> description;
    private Owner owner;

    public Proposal(String title, Optional<String> description, Owner owner) {
        this.id = new ProposalId();
        this.title = title;
        this.description = description;
        this.owner = owner;
    }

}
