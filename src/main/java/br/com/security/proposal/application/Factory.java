package br.com.security.proposal.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.security.proposal.application.enums.AcessScope;

@Component
public class Factory {

    private final Map<AcessScope, Strategy> strategyMap;

    public Factory(List<Strategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(Strategy::getAcessScope, strategy -> strategy));

    }

    public Strategy getStrategy(AcessScope scope) {
        return strategyMap.get(scope);
    }

}
