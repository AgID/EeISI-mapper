package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.RuleReport;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryRuleReport implements RuleReport {

    private List<Map.Entry<RuleOutcome, Rule>> items = new ArrayList<>();

    @Override public void store(RuleOutcome ruleOutcome, Rule rule) {
        items.add(new AbstractMap.SimpleEntry<>(ruleOutcome, rule) );
    }

    public String dump() {
        Map.Entry<RuleOutcome, Rule> k;
        return "Outcome,Reason\n" +
                items.stream()
                        .map(x -> x.getKey().outcome() + "," + x.getKey().description())
                        .collect(Collectors.joining("\n"));
    }
}
