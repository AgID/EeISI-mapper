package it.infocert.eigor.api.mapping;


import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.jdom2.Document;

import java.util.List;

public class GenericManyToOneTransformer extends GenericTransformer {
    private final String combinationExpression;
    private final String cenPath;
    private final List<String> xPaths;


    /**
     * Instantiates a new Generic many to one transformation.
     *
     * @param cenPath the CEN invoice path
     */
    public GenericManyToOneTransformer(String cenPath, String combinationExpression, List<String> xPaths, Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
        this.cenPath = cenPath;
        this.combinationExpression = combinationExpression;
        this.xPaths = xPaths;
        log = LoggerFactory.getLogger(GenericManyToOneTransformer.class);
    }



    @Override
    public void transformXmlToCen(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + xPaths + " - " + cenPath + ") ";
        log.info(logPrefix + "resolving");

        String finalValue = combinationExpression;
        for (int idx=0; idx<xPaths.size(); idx++){

            String xPathText = getNodeTextFromXPath(document, xPaths.get(idx));
            if (xPathText != null) {
                finalValue = finalValue.replace("%"+(idx+1), xPathText);
            }
        }

        if (finalValue.indexOf("%") != -1){
            errors.add(ConversionIssue.newWarning(new RuntimeException("Source element missing to complete expression: " + combinationExpression + "; Result: " + finalValue)));
        } else {
            addNewCenObjectFromStringValueToInvoice(cenPath, invoice, finalValue, errors);
        }
    }

    @Override
    public void transformCenToXml(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        throw new RuntimeException("Not yet implemented!");
    }
}
