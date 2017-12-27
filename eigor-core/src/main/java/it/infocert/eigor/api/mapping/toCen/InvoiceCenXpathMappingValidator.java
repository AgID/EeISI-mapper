package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BTBG;

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


    public InvoiceCenXpathMappingValidator(String keyRegex, IReflections reflections) {
        pattern = Pattern.compile(keyRegex);
        invoiceUtils = new InvoiceUtils(reflections);
    }

    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {
        for (String key : map.keySet()) {
            checkKey(key);
            for (String value : map.get(key)) {
                try {
                    checkValue(value);
                } catch (XPathExpressionException e) {
                    throw new SyntaxErrorInMappingFileException("Mapping '" + key + "' => '" + value + "' is wrong due to:" +  e.getMessage(),e);
                }
            }
        }
    }


    private void checkKey(String key) throws SyntaxErrorInMappingFileException {
        // TODO: CONFIG - CHECK THIS PATTERN MATCHING
        // if(!pattern.matcher(key).matches()){
        //     throw new RuntimeException("'" + key + "' does not match '" + pattern.pattern() + "'.");
        // }
        if(!validateBTsBGs(key)){
            throw new SyntaxErrorInMappingFileException("There isn't any BG / BT at path '" + key + "'.");
        }
    }

    /**
     * Check the value, throw an exception if the value is not valid.
     * The exception contains an explication why the value is wrong.
     * @return {@literal null} when the value is valid.
     */
    private void checkValue(String value) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.compile(value);
    }

    private boolean validateBTsBGs(String key) {
        String btName = key.substring(key.lastIndexOf("/") + 1);
        Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
        return btClass != null;
    }
}

