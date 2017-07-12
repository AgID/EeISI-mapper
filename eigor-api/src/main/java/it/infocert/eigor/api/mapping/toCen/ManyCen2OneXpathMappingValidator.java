package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import org.reflections.Reflections;

import java.util.regex.Pattern;

public class ManyCen2OneXpathMappingValidator implements InvoiceMappingValidator {

    private final Pattern patternXml;
    private final Pattern patternBgbt;
    private final InvoiceUtils invoiceUtils;

    public ManyCen2OneXpathMappingValidator(String keyRegexXml, String keyRegexBgbt, Reflections reflections) {
        patternXml = Pattern.compile(keyRegexXml);
        patternBgbt = Pattern.compile(keyRegexBgbt);
        invoiceUtils = new InvoiceUtils(reflections);
    }

    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {

    }
}
