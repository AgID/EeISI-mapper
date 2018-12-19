package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;

public class FieldLengthEnforcer {
    private static final Logger log = LoggerFactory.getLogger(FieldLengthEnforcer.class);

    private final Map<String, Integer> lengthsMap;
    private final ErrorCode.Location callingLocation;

    public FieldLengthEnforcer(File file, ErrorCode.Location callingLocation) {
        this.lengthsMap = loadFieldLengthsMap(file);
        this.callingLocation = callingLocation;
    }

    public ConversionResult<byte[]> process(byte[] xmlBytes) {
        ArrayList<IConversionIssue> issues = new ArrayList<>();

        Document document = getDocument(xmlBytes);

        checkAndTrimFieldLength(document, issues);

        if (issues.isEmpty()) {
            return new ConversionResult<>(issues, xmlBytes);
        }
        return new ConversionResult<>(issues, documentToBytes(document));
    }

    private void checkAndTrimFieldLength(Document document, ArrayList<IConversionIssue> issues) {
        final StringBuilder trimmedFieldsCsv = new StringBuilder();
        CSVPrinter printer = null;
        try {
            printer = new CSVPrinter(trimmedFieldsCsv, CSVFormat.DEFAULT.withHeader("XPath", "MaxLength", "Original value", "Trimmed value"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        for (String key : lengthsMap.keySet()) {
            Integer maxLength = lengthsMap.get(key);

            List<Element> matchingElements = getElementsByXPath(document, key);
            for (Element matchingElement : matchingElements) {
                String elementText = matchingElement.getText();
                if (elementText.length() > maxLength) {
                    matchingElement.setText(elementText.substring(0, maxLength));

                    try {
                        printer.printRecord(key, maxLength, elementText, matchingElement.getText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String message = String.format(
                            "Element '%s' original value '%s' exceeded maximum length of %d and has been trimmed.",
                            key, elementText, maxLength);
                    log.trace(message);
                    issues.add(ConversionIssue.newWarning(
                            new RuntimeException("Field exceeded maximum length."),
                            message,
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID
                    ));
                }
            }
        }
        if (!issues.isEmpty()) {
            try {
                printer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String csv = trimmedFieldsCsv.toString();

            Element allegati = new Element("Allegati")
                    .addContent(new Element("NomeAttachment").setText("trimmed_fields.csv"))
                    .addContent(new Element("FormatoAttachment").setText("csv"))
                    .addContent(new Element("Attachment").setText(DatatypeConverter.printBase64Binary(csv.getBytes())));

            document.getRootElement().getChild("FatturaElettronicaBody").addContent(allegati);
        }

    }

    private List<Element> getElementsByXPath(Document document, String xpath) {
        XPathFactory xPathFactory = XPathFactory.instance();
        try {
            XPathExpression<Element> expression = xPathFactory.compile("*/" + xpath, Filters.element());
            return expression.evaluate(document);
        } catch (Exception e) {
            throw new RuntimeException("Cannot evaluate expression '" + xpath + "'", e);
        }
    }

    private byte[] documentToBytes(Document document) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            new XMLOutputter(Format.getPrettyFormat()).output(document, bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    private Document getDocument(byte[] bytes) {
        Document doc;
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setIgnoringBoundaryWhitespace(true);
            doc = saxBuilder.build(new ByteArrayInputStream(bytes));
        } catch (JDOMException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return doc;
    }

    private Map<String, Integer> loadFieldLengthsMap(File file) {
        TreeMap<String, Integer> map = new TreeMap<>();
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                for (String key : properties.stringPropertyNames()) {
                    String property = properties.getProperty(key);
                    map.put(key, Integer.valueOf(property));
                }
            } finally {
                if (fileInputStream != null) fileInputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on loading field lengths file", e);
        }
        return map;
    }
}
