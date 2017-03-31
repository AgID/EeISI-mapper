package it.infocert.eigor.api;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ApiTest {

    @Test public void exampleOfAConversion() throws SyntaxInvoiceException {

        // services
        ReflectionBasedRepository reflectionBasedRepository = new ReflectionBasedRepository();
        CenRuleRepository ruleRepository = reflectionBasedRepository;
        ToCenConversionRepository conversionRepository = reflectionBasedRepository;
        FromCenConversionRepository fromCenConversionRepository = reflectionBasedRepository;

        // arguments
        String soureFormat = "fake";
        String targetFormat = "fake";
        InputStream invoiceInSourceFormat = new ByteArrayInputStream( "<invoice>this is an invoice</invoice>".getBytes() );

        // preconditions
        ToCENConversion toCen = conversionRepository.findConversionToCen(soureFormat);
        FromCENConverter fromCen = fromCenConversionRepository.findConversionFromCen(targetFormat);
        RuleReport ruleReport = new InMemoryRuleReport();

        // business logic
        BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat);
        ruleRepository.rules().forEach( rule -> {
            RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
            ruleReport.store( ruleOutcome, rule );
        });
        byte[] converted = fromCen.convert(cenInvoice);

    }

}
