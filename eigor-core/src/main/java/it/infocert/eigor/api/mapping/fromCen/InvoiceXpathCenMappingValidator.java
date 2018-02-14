package it.infocert.eigor.api.mapping.fromCen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BTBG;

import java.util.regex.Pattern;

/**
 * Validator for Map<CEN,XPATH>
 */
public class InvoiceXpathCenMappingValidator implements InvoiceMappingValidator {

    private final Pattern pattern;
    private final InvoiceUtils invoiceUtils;
    private final ErrorCode.Location callingLocation;


    public InvoiceXpathCenMappingValidator(String keyRegex, IReflections reflections, ErrorCode.Location callingLocation) {
        pattern = Pattern.compile(keyRegex);
        invoiceUtils = new InvoiceUtils(reflections);
        this.callingLocation = callingLocation;
    }

    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {
        for (String key : map.keySet()) {
            if (validateKey(key)) {
                for (String value : map.get(key)) {
                    if (!validateValue(value)) {
                        throw new SyntaxErrorInMappingFileException("Bad mapping value for key: " + key, callingLocation, ErrorCode.Action.CONFIG_VALIDATION);
                    }
                }
            } else throw new SyntaxErrorInMappingFileException("Bad mapping key: " + key, callingLocation, ErrorCode.Action.CONFIG_VALIDATION);
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

