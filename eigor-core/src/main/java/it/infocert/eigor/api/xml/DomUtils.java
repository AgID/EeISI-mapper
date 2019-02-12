package it.infocert.eigor.api.xml;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public final class DomUtils {

    /**
     * Converts a {@link org.jdom2.Document} into a pretty XML.
     */
    public static final String toPrettyXml(org.jdom2.Document jdomDocument) {
        StringWriter sw = new StringWriter();
        XMLOutputter xmlOutputter = new XMLOutputter();
        Format newFormat = Format.getPrettyFormat();
        xmlOutputter.setFormat(newFormat);
        try {
            xmlOutputter.output(jdomDocument, sw);
        } catch (IOException e) {
            // this should never happens, conversion runs in memory
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    /**
     * Convert a {@link org.w3c.dom.Document} into a pretty XML.
     */
    public static final String toPrettyXml(Document w3cDocument)  {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(w3cDocument),
                    new StreamResult(new OutputStreamWriter(out, "UTF-8")));
            return new String(out.toByteArray());
        } catch (TransformerException | UnsupportedEncodingException e) {
            // this should never happens, conversion runs in memory
            throw new RuntimeException(e);
        }
    }

}
