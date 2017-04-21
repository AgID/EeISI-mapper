package it.infocert.eigor.api;

import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ApiTest {

    private static Reflections reflections;

    @Before
    public void setUp() throws Exception {
        reflections = new Reflections("it.infocert");
    }

    @Test public void exampleOfAConversion() throws SyntaxErrorInInvoiceFormatException {

        // services
        ReflectionBasedRepository reflectionBasedRepository = new ReflectionBasedRepository(reflections);
        RuleRepository ruleRepository = reflectionBasedRepository;
        ToCenConversionRepository conversionRepository = reflectionBasedRepository;
        FromCenConversionRepository fromCenConversionRepository = reflectionBasedRepository;

        // arguments
        String soureFormat = "fake";
        String targetFormat = "fake";
        InputStream invoiceInSourceFormat = new ByteArrayInputStream( "<invoice>this is an invoice</invoice>".getBytes() );

        // preconditions
        ToCenConversion toCen = conversionRepository.findConversionToCen(soureFormat);
        FromCenConversion fromCen = fromCenConversionRepository.findConversionFromCen(targetFormat);
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
