package it.infocert.eigor.api.mapping.toCen;


import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.List;

public class GenericManyToOneTransformation extends GenericTransformation {
    private final String combinationExpression;
    private final String bgBtPath;
    private final List<String> xPaths;


    /**
     * Instantiates a new Generic many to one transformation.
     *
     * @param bgBtPath the CEN invoice path
     */
    public GenericManyToOneTransformation(String bgBtPath, String combinationExpression, List<String> xPaths, Reflections reflections) {
        super(reflections);
        this.bgBtPath = bgBtPath;
        this.combinationExpression = combinationExpression;
        this.xPaths = xPaths;
        log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);
    }

    @Override
    public void transform(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + xPaths + " - " + bgBtPath + ") ";
        log.info(logPrefix + "resolving");

        String finalValue = combinationExpression;
        for (int idx=0; idx<xPaths.size(); idx++){

            String xPathText = getNodeTextFromXPath(document, xPaths.get(idx));
            if (xPathText != null) {
                finalValue.replace("%"+(idx+1), xPathText);
            }
        }

        if (finalValue.indexOf("%") != -1){
            errors.add(ConversionIssue.newWarning(new RuntimeException("Source element missing to complete expression: " + combinationExpression + "; Result: " + finalValue)));
        } else {
            addNewCenObjectFromStringValueToInvoice(bgBtPath, invoice, finalValue, errors);
        }
    }
}
