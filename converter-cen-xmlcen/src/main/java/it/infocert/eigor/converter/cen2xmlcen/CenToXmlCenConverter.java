package it.infocert.eigor.converter.cen2xmlcen;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.DomUtils;
import it.infocert.eigor.api.xml.FilesystemXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;


public class CenToXmlCenConverter implements FromCenConversion {

    private XSDValidator xsdValidator;
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private static final ConverterUtils.NamingRules namingRules = new ConverterUtils.NamingRules("cen-xmlcen");
    private SchematronValidator schValidator;
    private boolean skipSchematron;

    public CenToXmlCenConverter(EigorConfiguration configuration) {
        this.configuration = checkNotNull( configuration );
        skipSchematron = false;
    }

    public void setSkipSchematronValidation(boolean b) {
        this.skipSchematron = b;
    }

    @Override
    public BinaryConversionResult convert(it.infocert.eigor.model.core.model.BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {

        Preconditions.checkState(xsdValidator!=null, "Converter not configured().");
        List<IConversionIssue> issues = new LinkedList<>();

        byte[] xmlBytes = toXml(invoice);

        issues.addAll( xsdValidator.validate(xmlBytes) );

        if(!skipSchematron)
            issues.addAll( schValidator.validate(xmlBytes) );


        if(issues.isEmpty()) {
            return new BinaryConversionResult(xmlBytes);
        }else{
            return new BinaryConversionResult(xmlBytes, issues);
        }


    }

    public byte[] toXml(BG0000Invoice invoice) {
        MyVisitor v = new MyVisitor();
        invoice.accept(v);
        byte[] xmlBytes;
        xmlBytes = v.getXml().getBytes();
        return xmlBytes;
    }

    @Override
    public boolean support(String format) {
        return "xmlcen".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<String>(Arrays.asList("xmlcen"));
    }

    @Override
    public String extension() {
        return "xml";
    }

    @Override
    public String getMappingRegex() {
        return null;
    }

    @Override
    public String getName() {
        return "converter-cen-xmlcen";
    }

    @Override
    public void configure() throws ConfigurationException {
        ErrorCode.Location callingLocation = ErrorCode.Location.XMLCEN_OUT;

        try {
            Source schemaSource = new StreamSource(getClass().getResourceAsStream("/converterdata/converter-commons/xmlcen/xsdstatic/semanticCEN0.0.3.xsd"));
            xsdValidator = new FilesystemXSDValidator(schemaSource, callingLocation);
        } catch (SAXException e) {
            throw new ConfigurationException("An error occurred while initializing the XSD validator", e);
        }

        try {
            schValidator = namingRules.getSchematronFromConfigOrFail("schematron", ErrorCode.Location.XMLCEN_OUT, configuration, drl);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

    }

    private static class MyVisitor implements Visitor {

        private Document invoice;
        private Deque<Element> stack = new LinkedList<Element>();

        @Override
        public void startInvoice(BG0000Invoice invoice) {
            this.invoice = new Document(new Element("SEMANTIC-INVOICE"));
            stack.push(this.invoice.getRootElement());
        }

        @Override
        public void endInvoice(BG0000Invoice invoice) {

        }

        @Override
        public void startBTBG(BTBG btbg) {

            BtBgName btbgName = BtBgName.parse(btbg.denomination());


            // if is a BG
            if (btbg.denomination().startsWith("BG")) {

                Element newElement = new Element(String.format("%s-%d", btbgName.bgOrBt().toUpperCase(), btbgName.number()));
                stack.peek().addContent(newElement);
                stack.push(newElement);

                // else is a bt
            } else {

                Element newElement = new Element(String.format("%s-%d", btbgName.bgOrBt().toUpperCase(), btbgName.number()));

                AbstractBT bt = (AbstractBT)btbg;
                Object btValue = bt.getValue();

                boolean done = false;

                if(!done) {
                    if (btbg instanceof BT0048BuyerVatIdentifier || btbg instanceof BT0063SellerTaxRepresentativeVatIdentifier
                    || btbg instanceof BT0090BankAssignedCreditorIdentifier
                    || btbg instanceof BT0137InvoiceLineAllowanceBaseAmount
                    || btbg instanceof BT0138InvoiceLineAllowancePercentage) {
                        done = true;
                        String theValue = ((AbstractBT) btbg).getValue().toString();
                        newElement.setText(theValue);
                    }
                    else if(btbg instanceof BT0018InvoicedObjectIdentifierAndSchemeIdentifier) {
                        done = true;
                        Identifier theValue = ((BT0018InvoicedObjectIdentifierAndSchemeIdentifier) btbg).getValue();
                        newElement.setText( theValue.getIdentifier() );
                        if(theValue.getIdentificationSchema()!=null && !theValue.getIdentificationSchema().isEmpty()) {
                            newElement.setAttribute("scheme", theValue.getIdentificationSchema());
                        }
                    }
                    else{
                        done = false;
                    }
                }

                if(!done) {
                    done = true;
                    if (btValue instanceof Identifier) {

                        Identifier id = ((Identifier) btValue);
                        String identificationSchema = id.getIdentificationSchema();
                        if (identificationSchema != null) {
                            newElement.setAttribute("scheme", identificationSchema);
                        } else {

                            if (!(btbg instanceof BT0046BuyerIdentifierAndSchemeIdentifier)) {
                                // Please check https://jira.infocert.it/browse/EISI-205 for details
                                // about why BT-46 "scheme" needs a special treatment
                                newElement.setAttribute("scheme", "");
                            }

                        }
                        String schemaVersion = id.getSchemaVersion();
                        if( schemaVersion!=null && !schemaVersion.isEmpty() ) {
                            newElement.setAttribute("version", schemaVersion);
                        }
                        newElement.setText(id.getIdentifier());

                    } else if (btValue instanceof Untdid5305DutyTaxFeeCategories) {
                        Untdid5305DutyTaxFeeCategories value = (Untdid5305DutyTaxFeeCategories) btValue;
                        newElement.setText(value.name());
                    } else if (btValue instanceof Untdid1001InvoiceTypeCode) {
                        Untdid1001InvoiceTypeCode value = (Untdid1001InvoiceTypeCode) btValue;
                        newElement.setText(String.valueOf(value.getCode()));
                    } else if (btValue instanceof Iso4217CurrenciesFundsCodes) {
                        Iso4217CurrenciesFundsCodes value = (Iso4217CurrenciesFundsCodes) btValue;
                        newElement.setText(String.valueOf(value.getCode()));
                    } else if (btValue instanceof Iso31661CountryCodes) {
                        Iso31661CountryCodes value = (Iso31661CountryCodes) btValue;
                        newElement.setText(String.valueOf(value.getIso2charCode()));
                    } else if(btValue instanceof Untdid4461PaymentMeansCode) {
                        Untdid4461PaymentMeansCode value = (Untdid4461PaymentMeansCode) btValue;
                        newElement.setText(String.valueOf(value.getCode()));
                    } else if(btValue instanceof Untdid7161SpecialServicesCodes) {
                        Untdid7161SpecialServicesCodes value = (Untdid7161SpecialServicesCodes) btValue;
                        newElement.setText(value.name());
                    } else if(btValue instanceof Untdid5189ChargeAllowanceDescriptionCodes) {
                        Untdid5189ChargeAllowanceDescriptionCodes value = (Untdid5189ChargeAllowanceDescriptionCodes) btValue;
                        newElement.setText(String.valueOf(value.getCode()));
                    } else if(btValue instanceof UnitOfMeasureCodes) {
                        UnitOfMeasureCodes value = (UnitOfMeasureCodes)btValue;
                        newElement.setText(String.valueOf(value.getCommonCode()));
                    } else {
                        done = false;
                    }
                }


                if(!done) {
                    done = true;
                    if (btbg instanceof BT0008ValueAddedTaxPointDateCode) {
                        String name = String.valueOf(((BT0008ValueAddedTaxPointDateCode) btbg).getValue().getCode());
                        newElement.setText(name);
                    } else if (btbg instanceof BT0055BuyerCountryCode) {
                        String name = String.valueOf(((BT0055BuyerCountryCode) btbg).getValue().getIso2charCode());
                        newElement.setText(name);
                    } else if (btbg instanceof BT0151InvoicedItemVatCategoryCode) {
                        String name = ((BT0151InvoicedItemVatCategoryCode) btbg).getValue().name();
                        newElement.setText(name);
                    } else if (btbg instanceof BT0118VatCategoryCode) {
                        String name = ((BT0118VatCategoryCode) btbg).getValue().name();
                        newElement.setText(name);
                    } else if (btbg instanceof BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename) {
                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename theBt = (BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename) btbg;
                        // <BT-125 mime="application/pdf" filename="filename0">ZGVmYXVsdA==</BT-125>
                        FileReference value = theBt.getValue();

                        if(value.getMimeType()!=null) {
                            newElement.setAttribute("mime", value.getMimeType().toString());
                        }

                        if(value.getFileName()!=null){
                            newElement.setAttribute("filename", value.getFileName());
                        }

                        try {
                            newElement.setText(IOUtils.toString(new FileInputStream(value.getFilePath()), "UTF-8"));
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
//                        // TODO: Base64
//                        try {
//                            byte[] base64Bytes = Base64.getEncoder().encode(IOUtils.toByteArray(new FileInputStream(value.getFilePath())));
//                            newElement.setText( new String(base64Bytes) );
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                    } else{
                        done = false;
                    }
                }

                if(!done) {
                    newElement.setText(btbg.toString());
                }
                stack.peek().addContent(newElement);
            }

        }

        @Override
        public void endBTBG(BTBG btbg) {
            if (btbg.denomination().startsWith("BG")) {
                stack.pop();
            }
        }

        public String getXml() {
            return DomUtils.toPrettyXml(this.invoice);
        }
    }
}
