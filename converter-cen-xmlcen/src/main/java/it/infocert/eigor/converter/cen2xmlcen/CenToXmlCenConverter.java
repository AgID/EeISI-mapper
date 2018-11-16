package it.infocert.eigor.converter.cen2xmlcen;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;


public class CenToXmlCenConverter implements FromCenConversion {

    private XSDValidator xsdValidator;

    @Override
    public BinaryConversionResult convert(it.infocert.eigor.model.core.model.BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {

        MyVisitor v = new MyVisitor();

        invoice.accept(v);

        byte[] xmlBytes;
        try {
            xmlBytes = v.getXml().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<IConversionIssue> issues = xsdValidator.validate(xmlBytes);

        if(issues.isEmpty()) {
            return new BinaryConversionResult(xmlBytes);
        }else{
            return new BinaryConversionResult(xmlBytes, issues);
        }


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
        return "xmlcen";
    }

    @Override
    public String getMappingRegex() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void configure() throws ConfigurationException {
        Source schemaSource = new StreamSource(getClass().getResourceAsStream("/converterdata/converter-commons/xmlcen/xsdstatic/semanticCEN0.0.2.xsd"));
        ErrorCode.Location callingLocation = ErrorCode.Location.XMLCEN_OUT;

        try {
            xsdValidator = new XSDValidator(schemaSource, callingLocation);
        } catch (SAXException e) {
            throw new ConfigurationException("An error occurred while initializing the XSD validator", e);
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


                if(btbg instanceof BT0034SellerElectronicAddressAndSchemeIdentifier) {
                    Identifier id = ((BT0034SellerElectronicAddressAndSchemeIdentifier) btbg).getValue();
                    newElement.setAttribute("scheme", id.getIdentificationSchema());
                }else if(btbg instanceof BT0151InvoicedItemVatCategoryCode) {
                    String name = ((BT0151InvoicedItemVatCategoryCode) btbg).getValue().name();
                    newElement.setText(name);
                } else if(btbg instanceof BT0118VatCategoryCode) {
                    String name = ((BT0118VatCategoryCode) btbg).getValue().name();
                    newElement.setText(name);
                }else{
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

        public String getXml() throws IOException {
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOutputter = new XMLOutputter();
            Format newFormat = Format.getPrettyFormat();
            xmlOutputter.setFormat(newFormat);
            xmlOutputter.output(this.invoice, sw);
            return sw.toString();
        }
    }
}
