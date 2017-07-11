package it.infocert.eigor.api.mapping;


import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Element;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.jdom2.Document;

import java.util.ArrayList;
import java.util.List;

public class GenericManyToOneTransformer extends GenericTransformer {
    private final String combinationExpression;
    private final String targetPath;
    private final List<String> sourcePaths;


    /**
     * Instantiates a new Generic many to one transformation.
     *
     * @param targetPath the CEN invoice path
     */
    public GenericManyToOneTransformer(String targetPath, String combinationExpression, List<String> sourcePaths, Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
        this.targetPath = targetPath;
        this.combinationExpression = combinationExpression;
        this.sourcePaths = sourcePaths;
        log = LoggerFactory.getLogger(GenericManyToOneTransformer.class);
    }



    @Override
    public void transformXmlToCen(Document document, BG0000Invoice invoice, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + sourcePaths + " - " + targetPath + ") ";
        log.trace(logPrefix + "resolving");

        String finalValue = combinationExpression;
        for (int i = 0; i< sourcePaths.size(); i++){

            String xPathText = getNodeTextFromXPath(document, sourcePaths.get(i));
            if (xPathText != null) {
                finalValue = finalValue.replace("%"+(i+1), xPathText);
            }
        }

        if (finalValue.contains("%")){
            errors.add(ConversionIssue.newWarning(new RuntimeException("Source element missing to complete expression: " + combinationExpression + "; Result: " + finalValue)));
        } else {
            addNewCenObjectFromStringValueToInvoice(targetPath, invoice, finalValue, errors);
        }
    }

    @Override
    public void transformCenToXml(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + sourcePaths + " - " + targetPath + ") ";
        log.trace(logPrefix + "resolving");

        String finalValue = combinationExpression;
        for (int idx = 0; idx< sourcePaths.size(); idx++){

            List<BTBG> bts = getAllBTs(sourcePaths.get(idx), invoice, errors);
            if (bts == null || bts.size() == 0) {
                log.warn("No BT found for {} when trying to map to {}", sourcePaths.get(idx), targetPath);
                return;
            }
            if (bts.size() > 1) {
                errors.add(ConversionIssue.newError(new RuntimeException("More than one BT for " + sourcePaths.get(idx) + ": " + bts)));
                return;
            }
            BTBG btbg = bts.get(0);
            Object value = getBtValue(btbg, errors);
            if (value != null) {
                Class<?> aClass = value.getClass();
                String converted = conversionRegistry.convert(aClass, String.class, value);
                log.info("CEN '{}' with value '{}' mapped to XML element '{}' with value '{}'.",
                        btbg.denomination(), String.valueOf(value), targetPath, converted);
                finalValue = finalValue.replace("%"+(idx+1), converted);
            }
        }

        if (finalValue.contains("%")){
            errors.add(ConversionIssue.newWarning(new RuntimeException("Source element missing to complete expression: " + combinationExpression + "; Result: " + finalValue)));
        } else {
            List<Element> elements = getAllXmlElements(targetPath, document, 1, sourcePaths.toString(), errors);
            if (elements == null) return;
            if (elements.size() > 1) {
                errors.add(ConversionIssue.newError(new RuntimeException("More than one element for " + targetPath + ": " + elements)));
                return;
            }
            elements.get(0).setText(finalValue);
        }
    }
}
