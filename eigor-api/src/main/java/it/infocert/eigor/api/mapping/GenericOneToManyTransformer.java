package it.infocert.eigor.api.mapping;

import com.helger.commons.collection.pair.Pair;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Generic class to transform both cen objects in XML elements and viceversa,
 * based on a 1-n configurable mapping
 * Use string substring start and end indexes pairs for each target
 */
public class GenericOneToManyTransformer extends GenericTransformer{

    private final List<String> targetPaths;
    private final String sourcePath;
    /**
     * Substring start and end index for each targetPath
     */
    private final Map<String,Pair<Integer, Integer>> splittingBoundsForTargetPath;

    public GenericOneToManyTransformer(Reflections reflections, ConversionRegistry conversionRegistry, List<String> targetPaths, String sourcePath, Map<String, Pair<Integer, Integer>> splittingBoundsForTargetPath) {
        super(reflections, conversionRegistry);
        this.targetPaths = targetPaths;
        this.sourcePath = sourcePath;
        this.splittingBoundsForTargetPath = splittingBoundsForTargetPath;
        log = LoggerFactory.getLogger(GenericOneToManyTransformer.class);
    }

    @Override
    public void transformXmlToCen(Document document, BG0000Invoice invoice, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        throw new RuntimeException("Not yet implemented!");
    }

    @Override
    public void transformCenToXml(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + sourcePath + " - " + targetPaths + ") ";
        log.trace(logPrefix + "resolving");


        List<BTBG> bts = getAllBTs(sourcePath, invoice, errors);
        if (bts == null || bts.size() == 0) return;
        if (bts.size() > 1) {
            errors.add(ConversionIssue.newError(new RuntimeException("More than one BT for " + sourcePath + ": " + bts)));
            return;
        }
        BTBG btbg = bts.get(0);
        Object value = getBtValue(btbg, errors);
        if (value != null) {
            Class<?> aClass = value.getClass();
            String converted = conversionRegistry.convert(aClass, String.class, value);
            log.info("CEN '{}' with value '{}' mapped to XML element '{}' with value '{}'.",
                    btbg.denomination(), String.valueOf(value), targetPaths, converted);

            splitCenValueAndApplyToXmlFields(converted, document, errors);
        }
    }

    private void splitCenValueAndApplyToXmlFields(String converted, Document document, List<ConversionIssue> errors) {
        for (int i = 0; i< targetPaths.size(); i++){

            List<Element> elements = getAllXmlElements(targetPaths.get(i), document, 1, sourcePath, errors);
            if (elements == null || elements.size() == 0) return;
            if (elements.size() > 1) {
                errors.add(ConversionIssue.newError(new RuntimeException("More than one element for " + targetPaths.get(i) + ": " + elements)));
                return;
            }

            // extract substring from converted
            Integer beginIndex = splittingBoundsForTargetPath.get(targetPaths.get(i)).getFirst();
            if (beginIndex != null) {
                errors.add(ConversionIssue.newError(new RuntimeException("Start index for " + targetPaths.get(i) + "is null!")));
                return;
            }
            Integer endIndex = splittingBoundsForTargetPath.get(targetPaths.get(i)).getSecond();
            String subStringValue = (endIndex == null) ? converted.substring(beginIndex) : converted.substring(beginIndex, endIndex);

            elements.get(0).setText(subStringValue);
        }
    }
}
