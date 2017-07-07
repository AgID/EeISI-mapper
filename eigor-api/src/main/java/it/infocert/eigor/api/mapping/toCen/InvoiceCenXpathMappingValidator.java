package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.regex.Pattern;

/**
 * Validator for Map<CEN,XPATH>
 */
public class InvoiceCenXpathMappingValidator implements InvoiceMappingValidator {

    private final Pattern pattern;
    private final InvoiceUtils invoiceUtils;


    public InvoiceCenXpathMappingValidator(String keyRegex, Reflections reflections) {
        pattern = Pattern.compile(keyRegex);
        invoiceUtils = new InvoiceUtils(reflections);
    }

    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {
        for (String key : map.keySet()) {
            if (validateKey(key)) {
                for (String value : map.get(key)) {
                    if (!validateValue(value)) {
                        throw new SyntaxErrorInMappingFileException("Bad mapping value for key: " + key);
                    }
                }
            } else throw new SyntaxErrorInMappingFileException("Bad mapping key: " + key);
        }
    }


    private boolean validateKey(String key) {
        return pattern.matcher(key).matches() && validateBTsBGs(key);
    }

    private boolean validateValue(String value) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            xpath.compile(value);
        } catch (XPathExpressionException e) {
            return false;
        }
        return true;
    }

    private boolean validateBTsBGs(String key) {
        String btName = key.substring(key.lastIndexOf("/") + 1);
        Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
        return btClass != null;
    }
}

