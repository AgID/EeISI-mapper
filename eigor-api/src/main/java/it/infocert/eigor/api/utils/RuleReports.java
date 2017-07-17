package it.infocert.eigor.api.utils;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Mapper;
import it.infocert.eigor.api.RuleReport;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import java.util.List;
import java.util.Map;

public class RuleReports {

    public static String dump(RuleReport ruleReport) {
        Mapper<Map.Entry<RuleOutcome, Rule>, String> mapper = new Mapper<Map.Entry<RuleOutcome, Rule>, String>() {
            @Override public String map(Map.Entry<RuleOutcome, Rule> x) {
                return x.getKey().outcome() + "," + x.getKey().description();
            }
        };
        List<String> stringPieces = Stream.create( ruleReport.getAll() ).map( mapper ).toList();
        StringBuffer sb = new StringBuffer("Outcome,Reason\n");
        for(int i = 0; i<stringPieces.size(); i++){
            sb.append(stringPieces.get(i));
            if(i<stringPieces.size()-1) sb.append("\n");
        }
        return sb.toString();
    }
}
