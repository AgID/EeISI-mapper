package it.infocert.eigor.converter.cen2xmlcen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Binary;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.Visitor;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static javafx.fxml.FXMLLoader.ROOT_TAG;

public class CenToXmlCenConverter implements FromCenConversion {

    @Override
    public BinaryConversionResult convert(it.infocert.eigor.model.core.model.BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {

        MyVisitor v = new MyVisitor();

        invoice.accept(v);

        try {
            return new BinaryConversionResult(v.getXml().getBytes());
        }catch(Exception e){

            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean support(String format) {
        return "xmlcen".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<String>( Arrays.asList( "xmlcen" ) );
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

    }

    private static class MyVisitor implements Visitor {

        private Document invoice;
        private Deque<Element> stack = new LinkedList<Element>();

        @Override
        public void startInvoice(BG0000Invoice invoice) {
            this.invoice = new Document( new Element("bg-0") );
            stack.push( this.invoice.getRootElement() );
        }

        @Override
        public void endInvoice(BG0000Invoice invoice) {

        }

        @Override
        public void startBTBG(BTBG btbg) {


            BtBgName btbgName = BtBgName.parse(btbg.denomination());


            // if is a BG
            if(btbg.denomination().startsWith("BG")) {

                Element newElement = new Element( String.format("%s-%d", btbgName.bgOrBt().toLowerCase(), btbgName.number()) );
                stack.peek().addContent(newElement);
                stack.push(newElement);

            // else is a bt
            }else{

                Element newElement = new Element( String.format("%s-%d", btbgName.bgOrBt().toLowerCase(), btbgName.number()) );
                newElement.setText( btbg.toString() );
                stack.peek().addContent(newElement);

            }

        }

        @Override
        public void endBTBG(BTBG btbg) {
            if(btbg.denomination().startsWith("BG")) {
                stack.pop();
            }
        }

        public String getXml() throws IOException {
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOutputter = new XMLOutputter();
            Format newFormat = Format.getPrettyFormat();
            xmlOutputter.setFormat(newFormat);
            xmlOutputter.output( this.invoice, sw);
            return sw.toString();
        }
    }
}
