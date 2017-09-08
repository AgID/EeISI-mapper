package it.infocert.eigor.api.mapping.fromCen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import java.util.regex.Pattern;

/**
 * Validator for Map<CEN,XPATH>
 */
public class InvoiceXpathCenMappingValidator implements InvoiceMappingValidator {

    private final Pattern pattern;
    private final InvoiceUtils invoiceUtils;


    public InvoiceXpathCenMappingValidator(String keyRegex, Reflections reflections) {
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
        return pattern.matcher(key).matches();
    }

    private boolean validateValue(String value) {

        if (value.isEmpty()) return false;

        String[] values = value.split("/");

        // first element should be empty in this case because our values start with '/', the token
        if (!values[0].isEmpty()) return false;


        for (int i = 1; i < values.length; i++) {
            if (!validateBTsBGs(values[i])) return false;
        }

        return true;
    }

    private boolean validateBTsBGs(String key) {
        String btName = key.substring(key.lastIndexOf("/") + 1);
        Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
        return btClass != null;
    }
}

