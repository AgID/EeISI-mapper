package it.infocert.eigor.api.mapping.toCen;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class does the generic one to one transformations
 */
public class GenericOneToOneTransformation extends GenericTransformation{

    private final String xPath;
    private final String bgBtPath;

    /**
     * Instantiates a new Generic one to one transformation.
     *
     * @param xPath    the Input invoice path
     * @param bgBtPath the CEN invoice path
     */
    public GenericOneToOneTransformation(String xPath, String bgBtPath, Reflections reflections) {
        super(reflections);
        this.xPath = xPath;
        this.bgBtPath = bgBtPath;
        log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    }

    /**
     * Transform.
     *
     * @param document the document
     * @param invoice  the invoice
     */
    public void transform(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + xPath + " - " + bgBtPath + ") ";
        log.info(logPrefix + "resolving");

        final String xPathText = getNodeTextFromXPath(document, xPath);
        if (xPathText != null) {
            addNewCenObjectFromStringValueToInvoice(bgBtPath, invoice, xPathText, errors);
        }
    }

}