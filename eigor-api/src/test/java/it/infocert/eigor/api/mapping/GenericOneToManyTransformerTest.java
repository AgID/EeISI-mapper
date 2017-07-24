package it.infocert.eigor.api.mapping;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0031SellerVatIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class GenericOneToManyTransformerTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToManyTransformerTest.class);
    private BG0000Invoice invoice;
    private ArrayList<IConversionIssue> errors;
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
    public void mappingToXml() throws Exception {
        final List<String> xPaths = Arrays.asList("/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        final String cenPath = "/BG0004/BT0031";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0031SellerVatIdentifier().add(new BT0031SellerVatIdentifier("IT000111222333"));
        invoice.getBG0004Seller().add(seller);

        Map<String, Pair<Integer,Integer>> indexLimits = new HashMap<>();
        indexLimits.put(xPaths.get(0), new Pair<Integer, Integer>(0,2));
        indexLimits.put(xPaths.get(1), new Pair<Integer, Integer>(2,14));

        GenericOneToManyTransformer transformer = new GenericOneToManyTransformer(reflections, conversionRegistry, xPaths, cenPath, indexLimits);
        transformer.transformCenToXml(invoice, document, errors);

        Element item = CommonConversionModule.evaluateXpath(document, xPaths.get(0)).get(0);
        assertEquals("IT", item.getText());

        item = CommonConversionModule.evaluateXpath(document, xPaths.get(1)).get(0);
        assertEquals("000111222333", item.getText());
    }

}
