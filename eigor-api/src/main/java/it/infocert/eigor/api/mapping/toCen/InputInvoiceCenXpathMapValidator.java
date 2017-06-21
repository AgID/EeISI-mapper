package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.Multimap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.regex.Pattern;

/**
 * Validator for Map<CEN,XPATH>
 */
public class InputInvoiceCenXpathMapValidator implements InputInvoiceMapValidator {

    private final Pattern pattern;

    public InputInvoiceCenXpathMapValidator(String keyRegex) {
        pattern = Pattern.compile(keyRegex);
    }

    @Override
    public void validate(Multimap<String, String> map) {
        for (String key : map.keySet()) {
            if (validateKey(key)) {
                for (String value : map.get(key)) {
                    if (!validateValue(value)) {
                        throw new RuntimeException("Bad mapping value for key: " + key);
                    }
                }
            } else throw new RuntimeException("Bad mapping key: " + key);
        }
    }


    private boolean validateKey(String key) {
        return pattern.matcher(key).matches();
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
}
