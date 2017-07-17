package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.RuleReport;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryRuleReport implements RuleReport {

    private List<Map.Entry<RuleOutcome, Rule>> items = new ArrayList<>();

    @Override public void store(RuleOutcome ruleOutcome, Rule rule) {
        items.add(new AbstractMap.SimpleEntry<>(ruleOutcome, rule) );
    }

    @Override
    public boolean hasFailures() {
        for(Map.Entry<RuleOutcome, Rule> item : items){
            RuleOutcome.Outcome outcome = item.getKey().outcome();
            if(outcome == RuleOutcome.Outcome.FAILED || outcome == RuleOutcome.Outcome.ERROR){
                return true;
            }
        }
        return false;
    }

    /**
     * Return the items in the report that are {@link RuleOutcome.Outcome#ERROR errors} or {@link RuleOutcome.Outcome#FAILED failues}.
     */
    @Override
    public List<Map.Entry<RuleOutcome, Rule>> getErrorsAndFailures() {
        ArrayList<Map.Entry<RuleOutcome, Rule>> list = new ArrayList<>();
        for (Map.Entry<RuleOutcome, Rule> item : items) {
            if(item.getKey().outcome()== RuleOutcome.Outcome.ERROR || item.getKey().outcome()== RuleOutcome.Outcome.FAILED){
                list.add(item);
            }
        }
        return list;
    }


}
