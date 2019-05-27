package it.infocert.eigor.cli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;


public class XxeChecker {
    public static boolean parser(Path filePath){

        String name;
        Logger log = LoggerFactory.getLogger(XxeChecker.class);


        log.warn("parser------->");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        try {
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
            // XML entity attacks are prevented
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // Disable external DTDs as well
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."

            // remaining parser logic
// Load XML file or stream using a XXE agnostic configured parser
            DocumentBuilder safebuilder = dbf.newDocumentBuilder();
            FileInputStream sourceInvoiceStream = new FileInputStream(filePath.toFile());
            log.warn("filepath:" + filePath);
            safebuilder.parse( sourceInvoiceStream);
            return true;
        } catch(Exception e){

            return false;
        }
//
    }
}
