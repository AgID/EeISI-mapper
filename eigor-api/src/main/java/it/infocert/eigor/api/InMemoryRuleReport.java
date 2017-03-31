package it.infocert.eigor.api;

import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryRuleReport implements RuleReport {

    private List<Pair<RuleOutcome, Rule>> items = new ArrayList<>();

    @Override public void store(RuleOutcome ruleOutcome, Rule rule) {
        items.add( new Pair<>(ruleOutcome, rule) );
    }

    public String dump() {
        return "Outcome,Reason\n" +
                items.stream()
                        .map(x -> x.getKey().outcome() + "," + x.getKey().description())
                        .collect(Collectors.joining("\n"));
    }
}
