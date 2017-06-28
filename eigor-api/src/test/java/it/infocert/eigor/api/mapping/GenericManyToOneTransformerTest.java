package it.infocert.eigor.api.mapping;

import com.google.common.io.Resources;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GenericManyToOneTransformerTest {
    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformer.class);
    private BG0000Invoice invoice;
    private ArrayList<ConversionIssue> errors;
    private Reflections reflections;
    private Document document;
    private SAXBuilder saxBuilder;
    private ConversionRegistry conversionRegistry;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();

        saxBuilder = new SAXBuilder();
        document = new Document(new Element("FatturaElettronica"));
        errors = new ArrayList<>(0);
        reflections = new Reflections("it.infocert");
        conversionRegistry = new ConversionRegistry(
                new CountryNameToIso31661CountryCodeConverter(),
                new LookUpEnumConversion(Iso31661CountryCodes.class),
                new StringToJavaLocalDateConverter("dd-MMM-yy"),
                new StringToJavaLocalDateConverter("yyyy-MM-dd"),
                new StringToUntdid1001InvoiceTypeCodeConverter(),
                new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),
                new StringToIso4217CurrenciesFundsCodesConverter(),
                new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
                new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
                new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),
                new StringToUnitOfMeasureConverter(),
                new LookUpEnumConversion(UnitOfMeasureCodes.class),
                new StringToDoubleConverter(),
                new StringToStringConverter(),
                new JavaLocalDateToStringConverter(),
                new JavaLocalDateToStringConverter("dd-MMM-yy"),
                new Iso4217CurrenciesFundsCodesToStringConverter(),
                new Iso31661CountryCodesToStringConverter(),
                new DoubleToStringConverter("#.00"),
                new UnitOfMeasureCodesToStringConverter()
        );
    }

    @Test
    public void mappingToCen() throws Exception {
        URL italianInvoiceUrl = Resources.getResource("examples/ubl/UBL-Invoice-2.1-Example.xml");
        Document doc = null;
        try {

            doc = saxBuilder.build(italianInvoiceUrl.toURI().toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        assert doc != null;

        final List<String> xPaths = Arrays.asList("/Invoice/ID", "/Invoice/IssueDate", "/Invoice/InvoiceTypeCode");
        final String cenPath = "/BG0004/BT0033";
        final String combinationExpression = "%1-%3 %2";

        GenericManyToOneTransformer transformator = new GenericManyToOneTransformer(cenPath, combinationExpression, xPaths, reflections, conversionRegistry);
        transformator.transformXmlToCen(doc, invoice, errors);

        assertThat(invoice.getBG0004Seller().get(0).getBT0033SellerAdditionalLegalInformation(), hasSize(1));
        assertEquals("TOSL108-380 2009-12-15", invoice.getBG0004Seller().get(0).getBT0033SellerAdditionalLegalInformation().get(0).toString());
    }
}
