package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ConversionIssueErrorCodeMapper;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.cen2fattpa.converters.*;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@SuppressWarnings("unchecked")
public class Cen2FattPA extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2FattPA.class);

    private static final String FPA_VERSION = "FPA12";

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.custom";


    private static final String FORMAT = "fatturapa";
    private final String ROOT_TAG = "FatturaElettronica";
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
            StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),
            StringToUnitOfMeasureConverter.newConverter(),
            LookUpEnumConversion.newConverter(UnitOfMeasureCodes.class),
            StringToDoubleConverter.newConverter(),
            StringToStringConverter.newConverter(),
            JavaLocalDateToStringConverter.newConverter(),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            Iso31661CountryCodesToStringConverter.newConverter(),
            DoubleToStringConverter.newConverter("#.00"),
            UnitOfMeasureCodesToStringConverter.newConverter(),
            Untdid1001InvoiceTypeCodeToItalianCodeStringConverter.newConverter(),
            Untdid4461PaymentMeansCodeToItalianCodeString.newConverter(),
            Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter.newConverter(),
            Untdid5305DutyTaxFeeCategoriesToItalianCodeStringConverter.newConverter(),
            Untdid7161SpecialServicesCodesToItalianCodeStringConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToItalianCodeConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter.newConverter(),
            IdentifierToStringConverter.newConverter()
    );
    private final ObjectFactory factory = new ObjectFactory();
    private XSDValidator validator;

    public Cen2FattPA(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
    }

    @Override
    public void configure() throws ConfigurationException {
        super.configure();

        String pathOfXsd = getConfiguration().getMandatoryString("eigor.converter.cen-fatturapa.xsd");
        Resource xsdFile = getResourceLoader().getResource(pathOfXsd);

        try {
            validator = new XSDValidator(xsdFile.getFile());
        } catch (IOException | SAXException e) {
            throw new ConfigurationException("An error occurred while configuring '" + this + "'.", e);
        }

        configurableSupport.configure();

    }

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        List<IConversionIssue> errors = new ArrayList<>(0);
        Document document = new Document();
        createRootNode(document);
        setFormatoTrasmissione(document);
        setProgressivoInvio(document);

        applyOne2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyMany2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyOne2ManyTransformationsBasedOnMapping(invoice, document, errors);
        byte[] documentByteArray = createXmlFromDocument(document, errors);
        BinaryConversionResult result = new BinaryConversionResult(documentByteArray, errors);

        byte[] xml = result.getResult();
        FatturaElettronicaType jaxbFattura = null;
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance("it.infocert.eigor.converter.cen2fattpa.models");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            jaxbFattura = ((JAXBElement<FatturaElettronicaType>) unmarshaller.unmarshal(new ByteArrayInputStream(xml))).getValue();
        } catch (JAXBException e) {
            errors.add(new ConversionIssueErrorCodeMapper(getName(), "Unmarshalling").map(ConversionIssue.newError(e)));
            log.error(e.getMessage(), e);
        }

        if (jaxbFattura != null) {
            applyCustomMapping(invoice, jaxbFattura, errors);

            if (jaxbFattura.getFatturaElettronicaBody().size() == 1) {
                setCondizioniPagamento(jaxbFattura.getFatturaElettronicaBody().get(0));
            } else {
                log.error("Wrong number of FatturaElettronicaBody in FatturaElettronica: expected 1, was {}", jaxbFattura.getFatturaElettronicaBody().size());
            }
        }

        JAXBElement<FatturaElettronicaType> fatturaElettronicaXML = factory.createFatturaElettronica(jaxbFattura);

        StringWriter xmlOutput = null;

        try {
            if (jaxbContext != null) {
                xmlOutput = new StringWriter();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(fatturaElettronicaXML, xmlOutput);
            }
        } catch (JAXBException e) {
            errors.add(new ConversionIssueErrorCodeMapper(getName(), "Marshalling").map(ConversionIssue.newError(e)));
            log.error(e.getMessage(), e);
        }
        if (xmlOutput == null) {
            return result;
        } else {

            byte[] jaxml = xmlOutput.toString().getBytes();
            List<IConversionIssue> validationErrors = validator.validate(jaxml);
            validationErrors = new ConversionIssueErrorCodeMapper(getName(), "XSDValidation").mapAll(validationErrors);
            if (validationErrors.isEmpty()) {
                log.info("XSD validation successful!");
            }

            errors.addAll(validationErrors);
            new ConversionIssueErrorCodeMapper(getName()).mapAll(errors);
            return new BinaryConversionResult(jaxml, errors);

        }
    }

    private void applyCustomMapping(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        List<CustomMapping<FatturaElettronicaType>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<FatturaElettronicaType> customMapping : customMappings) {
            customMapping.map(invoice, fatturaElettronica, errors);
        }
    }

    @Override
    public boolean support(String format) {
        return IConstants.CONVERTER_SUPPORT.equals(format.toLowerCase().trim());
    }

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
        return "\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*";
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

    private void createRootNode(Document doc) {
        Element root = new Element(ROOT_TAG, Namespace.getNamespace("nx", "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("ds", "http://www.w3.org/2000/09/xmldsig#"));
        root.addNamespaceDeclaration(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        root.setAttribute("versione", "FPA12");
        root.addContent(new Element("FatturaElettronicaHeader"));
        root.addContent(new Element("FatturaElettronicaBody"));
        doc.setRootElement(root);
    }

    private void setFormatoTrasmissione(Document doc) {
        Element datiTrasmissione = new Element("DatiTrasmissione");
        Element formatoTrasmissione = new Element("FormatoTrasmissione");

        formatoTrasmissione.setText(FPA_VERSION);

        datiTrasmissione.addContent(formatoTrasmissione);
        doc.getRootElement().getChild("FatturaElettronicaHeader").addContent(datiTrasmissione);
    }

    private void setProgressivoInvio(Document doc) {
        Element progressivoInvio = new Element("ProgressivoInvio");
        progressivoInvio.setText("00001");
        doc.getRootElement().getChild("FatturaElettronicaHeader").getChild("DatiTrasmissione").addContent(progressivoInvio);
    }

    //FIXME move to custom converter
    private void setCondizioniPagamento(FatturaElettronicaBodyType body) {
        List<DatiPagamentoType> datiPagamento = body.getDatiPagamento();
        if (!datiPagamento.isEmpty()) {
            for (DatiPagamentoType dati : datiPagamento) {
                dati.setCondizioniPagamento(CondizioniPagamentoType.TP_02);
            }
        }
    }

    @Override
    public String getName() {
        return "converter-cen-fattpa";
    }
}
