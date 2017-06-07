package it.infocert.eigor.api.impl;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Mapper;
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

    public String dump() {

        Mapper<Map.Entry<RuleOutcome, Rule>, String> mapper = new Mapper<Map.Entry<RuleOutcome, Rule>, String>() {
            @Override public String map(Map.Entry<RuleOutcome, Rule> x) {
                return x.getKey().outcome() + "," + x.getKey().description();
            }
        };
        List<String> stringPieces = Stream.create( items ).map( mapper ).toList();

        StringBuffer sb = new StringBuffer("Outcome,Reason\n");
        for(int i = 0; i<stringPieces.size(); i++){
            sb.append(stringPieces.get(i));
            if(i<stringPieces.size()-1) sb.append("\n");
        }
        return sb.toString();

    }
}
