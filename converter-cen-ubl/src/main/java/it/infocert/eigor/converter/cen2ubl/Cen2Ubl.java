package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ConversionIssueErrorCodeMapper;
import it.infocert.eigor.converter.cen2ubl.converters.Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2ubl.converters.Untdid4461PaymentMeansCodeToItalianCodeString;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Cen2Ubl extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2Ubl.class);

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.custom";

    private static final String FORMAT = "ubl";

    private final String CBC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private final String CAC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new StringToStringConverter(),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new JavaLocalDateToStringConverter(),
            new Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter(),
            new Untdid1001InvoiceTypeCodesToStringConverter(),
            new DoubleToStringConverter("#.00"),
            new Iso31661CountryCodesToStringConverter(),
            new IdentifierToStringConverter(),
            new Untdid4461PaymentMeansCodeToItalianCodeString()
    );

    public Cen2Ubl(Reflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
    }


    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {
        List<IConversionIssue> errors = new ArrayList<>(0);
        Document document = new Document();
        createRootNode(document);

//        Element root = document.getRootElement();
//        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer("/Invoice/ID", "/BT-1", null, conversionRegistry);
//        transformer.transformCenToXml(invoice, document, errors);

        applyOne2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyMany2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyOne2ManyTransformationsBasedOnMapping(invoice, document, errors);
        applyCustomMapping(invoice, document, errors);

        new XmlNamespaceApplier(CBC_URI, CAC_URI).applyUblNamespaces(document);
        new ConversionIssueErrorCodeMapper(getName()).mapAll(errors);

        byte[] documentByteArray = createXmlFromDocument(document, errors);
        BinaryConversionResult result = new BinaryConversionResult(documentByteArray, errors);

        return result;
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors);
        }
    }

    @Override
    public boolean support(String format) { return "ubl".equals(format.toLowerCase().trim()); }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Collections.singletonList(FORMAT));
    }

    @Override
    public String extension() {
        return "xml";
    }

    @Override
    public String getMappingRegex() {
        return "\\/Invoice(\\/\\w+(\\[\\])*)*";
    }

    @Override
    protected String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    protected String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return ONE2MANY_MAPPING_PATH;
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
    }

    @Override
    public String getName() {
        return "converter-cen-ubl";
    }




    private void createRootNode(Document doc) {
        Element root = new Element("Invoice");
        root.addNamespaceDeclaration(Namespace.getNamespace("cac", CAC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("cbc", CBC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("udt", "urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("ccts", "urn:un:unece:uncefact:documentation:2"));
//        root.addNamespaceDeclaration(Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"));
        doc.setRootElement(root);
    }
}

