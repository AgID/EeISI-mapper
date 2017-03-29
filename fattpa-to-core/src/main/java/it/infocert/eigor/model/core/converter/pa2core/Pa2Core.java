package it.infocert.eigor.model.core.converter.pa2core;

import it.infocert.eigor.model.core.converter.pa2core.mapping.CedentePrestatoreToBG04;
import it.infocert.eigor.model.core.converter.pa2core.mapping.IdFiscaleIvaToBT63;
import it.infocert.eigor.model.core.converter.pa2core.mapping.NumeroFatturaToBT01;
import it.infocert.eigor.model.core.converter.pa2core.mapping.RappresentanteFiscaleToBG11;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Pa2Core {

    public BG0000Invoice convert(String uri) {
        BG0000Invoice coreInvoice = new BG0000Invoice();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(uri);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        assert doc != null;
        doc.getDocumentElement().normalize();
        NumeroFatturaToBT01.convertTo(doc, coreInvoice);
        RappresentanteFiscaleToBG11.convertTo(doc, coreInvoice);
        IdFiscaleIvaToBT63.convertTo(doc, coreInvoice);
        CedentePrestatoreToBG04.convertTo(doc, coreInvoice);
        return coreInvoice;
    }

//    public BG0000Invoice convert(String uri) {
//        BG0000Invoice coreInvoice = new BG0000Invoice();
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        Document doc = null;
//        try {
//            DocumentBuilder dBuilder = factory.newDocumentBuilder();
//            doc = dBuilder.parse(uri);
//        } catch (SAXException | IOException | ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//
//        assert doc != null;
//        doc.getDocumentElement().normalize();
//        NodeList datiGeneraliDocumento = doc.getElementsByTagName("DatiGeneraliDocumento");
//        Node item = datiGeneraliDocumento.item(0);
//
//        if (item.getNodeType() == Node.ELEMENT_NODE) {
//            Element eElement = (Element) item;
//            NodeList numero = eElement.getElementsByTagName("Numero");
//            coreInvoice.getBT01InvoiceNumbers().add(new BT01InvoiceNumber(new Identifier(numero.item(0).getTextContent())));
//        }
//
//        return coreInvoice;
//    }
//
//    public BG0000Invoice convertXPath(String uri) {
//        BG0000Invoice coreInvoice = new BG0000Invoice();
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        Document doc = null;
//        try {
//            DocumentBuilder dBuilder = factory.newDocumentBuilder();
//            doc = dBuilder.parse(uri);
//        } catch (SAXException | IOException | ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//
//        assert doc != null;
//        doc.getDocumentElement().normalize();
//        XPathFactory xpathfactory = XPathFactory.newInstance();
//        XPath xpath = xpathfactory.newXPath();
//
//        XPathExpression expr = null;
//        Object result = null;
//        try {
//            expr = xpath.compile("//FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero");
//            result = expr.evaluate(doc, XPathConstants.NODESET);
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }
//        NodeList nodes = (NodeList) result;
//        assert nodes != null;
//        coreInvoice.getBT01InvoiceNumbers().add(new BT01InvoiceNumber(new Identifier(nodes.item(0).getTextContent())));
//
//        return coreInvoice;
//    }
}