package it.infocert.eigor.api;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


public class ApiTest {

    @Test public void exampleOfAConversion() throws SyntaxErrorInInvoiceFormatException {

        // services
        ReflectionBasedRepository reflectionBasedRepository = new ReflectionBasedRepository(new Reflections("it.infocert.eigor.api"));
        RuleRepository ruleRepository = new ReflectionBasedRepository( new Reflections("it.infocert.eigor.model") );
        ToCenConversionRepository conversionRepository = reflectionBasedRepository;
        FromCenConversionRepository fromCenConversionRepository = reflectionBasedRepository;

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

        Stream.create(rules).forEach(new Consumer<Rule>() {
            @Override public void consume(Rule rule) {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            }
        });

        byte[] converted = fromCen.convert(cenInvoice).getResult();

    }

}
