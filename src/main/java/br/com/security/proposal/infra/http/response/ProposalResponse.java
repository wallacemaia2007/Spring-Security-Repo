package br.com.security.proposal.infra.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.security.proposal.application.output.ProposalOutput;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProposalResponse(String id, String title, String description, OwerResponse ower) {

    public static ProposalResponse from(ProposalOutput output) {
        return new ProposalResponse(
                output.id(),
                output.title(),
                output.description().orElse(null),
                new OwerResponse(output.ownerId(), output.ownerName()));
    }

}
