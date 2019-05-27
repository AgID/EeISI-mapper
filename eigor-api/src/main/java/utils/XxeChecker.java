package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;


public class XxeChecker {

    private static DocumentBuilder getSafeDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf.newDocumentBuilder();
    }

    public static boolean parser(Path filePath) {
        Logger log = LoggerFactory.getLogger(XxeChecker.class);

        try {
            DocumentBuilder safebuilder = getSafeDocumentBuilder();
            FileInputStream sourceInvoiceStream = new FileInputStream(filePath.toFile());
            log.warn("filepath:" + filePath);
            safebuilder.parse(sourceInvoiceStream);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parser(String invoice) {
        try {
            DocumentBuilder safebuilder = getSafeDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(invoice.getBytes());
            safebuilder.parse(byteArrayInputStream);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parser(InputStream invoice) {
        try {
            DocumentBuilder safebuilder = getSafeDocumentBuilder();
            safebuilder.parse(invoice);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}