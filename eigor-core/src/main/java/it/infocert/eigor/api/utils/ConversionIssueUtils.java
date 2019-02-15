package it.infocert.eigor.api.utils;

import it.infocert.eigor.api.IConversionIssue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.util.List;

public final class ConversionIssueUtils {

    /** Dump the provided list of issues in a CSV format. */
    public static String toCsv(List<IConversionIssue> issues) {
        final StringBuilder toCenErrorsCsv = new StringBuilder();
        try (CSVPrinter printer = new CSVPrinter(toCenErrorsCsv, CSVFormat.DEFAULT.withHeader("Fatal", "Error", "Reason"));) {
            if(issues!=null) {
                for (IConversionIssue e : issues) {
                    printer.printRecord(e.isError(), e.getMessage(), e.getCause());
                }
            }
            printer.flush();
        } catch (Exception e) {
            toCenErrorsCsv.append(e.getMessage());
        }
        return toCenErrorsCsv.toString();
    }

    private ConversionIssueUtils() {
        throw new UnsupportedOperationException("Not meant to be instantiated");
    }

}
