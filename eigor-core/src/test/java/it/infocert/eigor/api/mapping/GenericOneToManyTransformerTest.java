package it.infocert.eigor.api.mapping;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GenericOneToManyTransformerTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToManyTransformerTest.class);
    private BG0000Invoice invoice;
    private ArrayList<IConversionIssue> errors;
    private IReflections reflections;
    private Document document;
    private SAXBuilder saxBuilder;
    private ConversionRegistry conversionRegistry;
    private List<String> xPaths;
    private String cenPath;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();

        saxBuilder = new SAXBuilder();
        document = new Document(new Element("FatturaElettronica"));
        errors = new ArrayList<>(0);
        reflections = new JavaReflections();
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
        GenericOneToManyTransformer transformer = createTransformer(new Pair<>(0, 2), new Pair<>(2, 14));
        transformer.transformCenToXml(invoice, document, errors);

        Element item = CommonConversionModule.evaluateXpath(document, xPaths.get(0)).get(0);
        assertEquals("IT", item.getText());

        item = CommonConversionModule.evaluateXpath(document, xPaths.get(1)).get(0);
        assertEquals("000111222333", item.getText());
    }

    @Test
    public void shouldFailGracefullyIfEndIndexExceedStringLength() throws Exception {
        GenericOneToManyTransformer transformer = createTransformer(new Pair<>(0, 2), new Pair<>(2, 20));
        transformer.transformCenToXml(invoice, document, errors);

        int itemsNumber = CommonConversionModule.evaluateXpath(document, xPaths.get(1)).size();

        assertThat(itemsNumber, is(0));
        assertThat(errors.size(), is(1));
        assertEquals("String index out of range: 20", errors.get(0).getErrorMessage().getMessage());
    }

    @SafeVarargs
    private final GenericOneToManyTransformer createTransformer(Pair<Integer, Integer>... bounds) {
        this.xPaths = Arrays.asList("/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        this.cenPath = "/BG0004/BT0031";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0031SellerVatIdentifier().add(new BT0031SellerVatIdentifier("IT000111222333"));
        invoice.getBG0004Seller().add(seller);

        Map<String, Pair<Integer, Integer>> indexLimits = new HashMap<>();
        indexLimits.put(xPaths.get(0), bounds[0]);
        indexLimits.put(xPaths.get(1), bounds[1]);

        return new GenericOneToManyTransformer(reflections, conversionRegistry, xPaths, cenPath, indexLimits);
    }
}
