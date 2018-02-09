package it.infocert.eigor.api.utils;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.RuleReport;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.Map;

public class RuleReports {

    public static String dump(RuleReport ruleReport) {
        final StringBuffer sb = new StringBuffer();
        try (final CSVPrinter printer = new CSVPrinter(sb, CSVFormat.DEFAULT.withHeader("Outcome", "Reason"));) {
            Consumer<Map.Entry<RuleOutcome, Rule>> entryConsumer = new Consumer<Map.Entry<RuleOutcome, Rule>>() {
                @Override
                public void consume(Map.Entry<RuleOutcome, Rule> ruleOutcomeRuleEntry) {
                    try {
                        printer.printRecord(ruleOutcomeRuleEntry.getKey().outcome(), ruleOutcomeRuleEntry.getKey().description());
                    } catch (IOException e) {
                        sb.append(e.getMessage());
                    }
                }
            };
            Stream.create( ruleReport.getAll() ).forEach( entryConsumer );
            printer.flush();
            printer.close();
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
