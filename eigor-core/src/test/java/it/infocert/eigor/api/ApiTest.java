package it.infocert.eigor.api;



import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.impl.*;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;


public class ApiTest {

    @Test public void exampleOfAConversion() throws SyntaxErrorInInvoiceFormatException {

        // services
        IReflections reflections = new JavaReflections();
        RuleRepository ruleRepository = new ReflectionBasedRepository( new Reflections("it.infocert.eigor.model") );
        PropertiesBackedConfiguration configuration = new PropertiesBackedConfiguration();

        ToCenConversionRepository conversionRepository = new ToCenListBakedRepository(
                new FakeToCenConversion(reflections, configuration)
        );
        FromCenConversionRepository fromCenConversionRepository = new FromCenListBakedRepository(
                new FakeFromCenConversion(reflections, configuration)
        );

        // arguments
        String soureFormat = "fake";
        String targetFormat = "fake";
        InputStream invoiceInSourceFormat = new ByteArrayInputStream( "<invoice>this is an invoice</invoice>".getBytes() );

        // preconditions
        ToCenConversion toCen = conversionRepository.findConversionToCen(soureFormat);
        FromCenConversion fromCen = fromCenConversionRepository.findConversionFromCen(targetFormat);
        final RuleReport ruleReport = new InMemoryRuleReport();

        // business logic
        final BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat).getResult();
        List<Rule> rules = ruleRepository.rules();

        rules.stream().forEach(rule -> {
            RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
            ruleReport.store(ruleOutcome, rule);
        });

        byte[] converted = fromCen.convert(cenInvoice).getResult();

    }

}
