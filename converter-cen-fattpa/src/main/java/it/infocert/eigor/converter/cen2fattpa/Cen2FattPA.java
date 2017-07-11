package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid1001InvoiceTypeCodeToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid4461PaymentMeansCodeToItalianCodeString;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.converter.cen2fattpa.models.ObjectFactory;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

@SuppressWarnings("unchecked")
public class Cen2FattPA extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2FattPA.class);

    private static final String FPA_VERSION = "FPA12";
    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-fatturapa.mapping.many-to-one";
    private static final String FORMAT = "fatturapa";
    private final String ROOT_TAG = "FatturaElettronica";
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion(Iso31661CountryCodes.class),
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
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new Iso31661CountryCodesToStringConverter(),
            new DoubleToStringConverter("#.00"),
            new UnitOfMeasureCodesToStringConverter(),
            new Untdid1001InvoiceTypeCodeToItalianCodeStringConverter(),
            new Untdid4461PaymentMeansCodeToItalianCodeString(),
            new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter()
    );
    private final ObjectFactory factory = new ObjectFactory();
    private XSDValidator validator;

    public Cen2FattPA(Reflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
        setMappingRegex("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*");
    }

    @Override public void configure() throws ConfigurationException {
        super.configure();

        String pathOfXsd = getConfiguration().getMandatoryString("eigor.converter.cen-fatturapa.xsd");
        Resource xsdFile = getResourceLoader().getResource(pathOfXsd);

        InputStream xsdStream = null;
        try {
            xsdStream = xsdFile.getInputStream();
            validator = new XSDValidator(xsdStream);
        } catch (IOException | SAXException e) {
            throw new ConfigurationException("An error occurred while configuring '" + this + "'.", e);
        } finally {
            if(xsdStream!=null) {
                try {
                    xsdStream.close();
                } catch (IOException e) {
                    log.warn("Unable to close stream for resource '{}'.", pathOfXsd);
                }
            }
        }

        configurableSupport.configure();

    }

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        List<ConversionIssue> errors = new ArrayList<>(0);
        Document document = new Document();
        createRootNode(document);
        setFormatoTrasmissione(document);
        setProgressivoInvio(document);
        //TODO Add here hardcoded conversion
        Pair<Document, List<ConversionIssue>> oneToOneResult = applyOne2OneTransformationsBasedOnMapping(invoice, document, errors);
        BinaryConversionResult result = applyMany2OneTransformationsBasedOnMapping(invoice, oneToOneResult.getLeft(), oneToOneResult.getRight());

        byte[] xml = result.getResult();
        FatturaElettronicaType jaxbFattura = null;
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance("it.infocert.eigor.converter.cen2fattpa.models");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            jaxbFattura = ((JAXBElement<FatturaElettronicaType>) unmarshaller.unmarshal(new ByteArrayInputStream(xml))).getValue();
        } catch (JAXBException e) {
            errors.add(ConversionIssue.newError(e));
            log.error(e.getMessage(), e);
        }

        if (jaxbFattura != null) {
            BodyFatturaConverter bfc = new BodyFatturaConverter(jaxbFattura.getFatturaElettronicaBody().remove(0), factory, invoice, errors);
            bfc.setConversionRegistry(conversionRegistry);
            bfc.computeMultipleCenElements2FpaField();
            jaxbFattura.getFatturaElettronicaBody().add(bfc.getFatturaElettronicaBody());
        }

        JAXBElement<FatturaElettronicaType> fatturaElettronicaXML = factory.createFatturaElettronica(jaxbFattura);

        StringWriter xmlOutput = null;

        try {
            if (jaxbContext != null) {
                xmlOutput = new StringWriter();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.marshal(fatturaElettronicaXML, xmlOutput);
            }
        } catch (JAXBException e) {
            errors.add(ConversionIssue.newError(e));
            log.error(e.getMessage(), e);
        }
        if (xmlOutput == null) {
            return result;
        } else {
            byte[] jaxml = xmlOutput.toString().getBytes();
            List<ConversionIssue> validationErrors = validator.validate(jaxml);
            if (validationErrors.isEmpty()) {
                log.info("XSD validation successful!");
            }

            errors.addAll(validationErrors);
            return new BinaryConversionResult(jaxml, errors);

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
    protected String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    protected String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }

    @Override protected String getOne2ManyMappingPath() {
        return null;
    }

    private void createRootNode(Document doc) {
        Element root = new Element(ROOT_TAG, Namespace.getNamespace("nx" , "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("ds" , "http://www.w3.org/2000/09/xmldsig#"));
        root.addNamespaceDeclaration(Namespace.getNamespace("xsi" , "http://www.w3.org/2001/XMLSchema-instance"));
        root.setAttribute("versione" , "FPA12");
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

    @Override
    public String getName() {
        return "cen-fatturapa";
    }
}