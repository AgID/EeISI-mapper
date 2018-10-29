package it.infocert.eigor.api.utils;


import it.infocert.eigor.api.RuleReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class RuleReports {

    public static String dump(RuleReport ruleReport) {
        final StringBuffer sb = new StringBuffer();
        try (final CSVPrinter printer = new CSVPrinter(sb, CSVFormat.DEFAULT.withHeader("Outcome", "Reason"));) {
            ruleReport.getAll().stream().forEach( ruleOutcomeRuleEntry -> {
                try {
                    printer.printRecord(ruleOutcomeRuleEntry.getKey().outcome(), ruleOutcomeRuleEntry.getKey().description());
                } catch (IOException e) {
                    sb.append(e.getMessage());
                }
            }  );

            printer.flush();
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
