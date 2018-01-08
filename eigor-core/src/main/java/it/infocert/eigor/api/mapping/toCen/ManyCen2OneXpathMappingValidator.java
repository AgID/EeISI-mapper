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
import java.util.HashMap;
import java.util.regex.Pattern;

public class ManyCen2OneXpathMappingValidator implements InvoiceMappingValidator {

    private final Pattern patternXml;
    private final Pattern patternBgbt;
    private final InvoiceUtils invoiceUtils;

    public ManyCen2OneXpathMappingValidator(String keyRegexXml, String keyRegexBgbt, IReflections reflections) {
        patternXml = Pattern.compile(keyRegexXml);
        patternBgbt = Pattern.compile(keyRegexBgbt);
        invoiceUtils = new InvoiceUtils(reflections);
    }

    /**
     * mapping.1221_Indirizzo.type=concatenation
     * mapping.1221_Indirizzo.source.1=/BG0004/BG0005/BT0035
     * mapping.1221_Indirizzo.source.2=/BG0004/BG0005/BT0036
     * mapping.1221_Indirizzo.source.3=/BG0004/BG0005/BT0162
     * mapping.1221_Indirizzo.target=/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo
     * mapping.1221_Indirizzo.expression=%1 %2 %3
     * @param map the mappings map
     * @throws SyntaxErrorInMappingFileException
     */
    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {
        HashMap<String, Integer> mappingSource = new HashMap<>();
        HashMap<String, String> mappingTarget = new HashMap<>();
        HashMap<String, String> mappingExpr = new HashMap<>();
        for (String key : map.keySet()) {
            if (key.contains(".source.") && key.matches("^.*\\d$")) {
                int idx = key.indexOf(".source.");
                String typeKey = key.substring(0,idx).concat(".type");
                Integer val = mappingSource.get(typeKey);
                try {
                    int newVal = Integer.parseInt(key.substring(idx + ".source.".length()));
                    if (val == null || newVal > val.intValue()){
                        mappingSource.put(typeKey, newVal);
                    }
                } catch (NumberFormatException e) {
                    throw new SyntaxErrorInMappingFileException("Invalid index for source element: " + key, e);
                }
            }
            if (key.endsWith(".type")){
                if (!mappingSource.containsKey(key)) {
                    mappingSource.put(key, null);
                }
                if (!mappingTarget.containsKey(key)) {
                    mappingTarget.put(key, null);
                }
            }
            if (key.endsWith(".target")){
                int idx = key.indexOf(".target");
                mappingTarget.put(key.substring(0,idx).concat(".type"), key);
            }
            if (validateKey(key)) {
                for (String value : map.get(key)) {
                    if (key.endsWith(".expression")) {
                        int idx = key.indexOf(".expression");
                        mappingExpr.put(key.substring(0,idx).concat(".type"), value);
                    }

                    if (!validateValue(value, key)) {
                        throw new SyntaxErrorInMappingFileException("Bad mapping value for key: " + key + " - " + value);
                    }
                }
            } else throw new SyntaxErrorInMappingFileException("Bad mapping key: " + key);
        }
        for (String key: mappingSource.keySet()){
            if (mappingSource.get(key) == null){
                throw new SyntaxErrorInMappingFileException("Missign source key for mapping: " + key);
            }
            if (!mappingExpr.containsKey(key)) {
                throw new SyntaxErrorInMappingFileException("Missign expression key for mapping: " + key);
            }
            /*
                mappingSource contains maximum index in source fields
                i search to see if index present in expression
             */
            if (mappingExpr.get(key).indexOf("%"+mappingSource.get(key)) == -1){
                throw new SyntaxErrorInMappingFileException("Source element with index "+ mappingSource.get(key) + " missing from expression: " + mappingExpr.get(key));
            }
        }
        for (String key: mappingTarget.keySet()){
            if (mappingTarget.get(key) == null){
                throw new SyntaxErrorInMappingFileException("Missign target key for mapping: " + key);
            }
        }
    }

    private boolean validateKey(String key) {
        if (key.endsWith("type") ||
                key.endsWith(".target") ||
                key.endsWith(".expression")) {
            return true;
        }
        if (key.contains(".source.") &&
                key.matches("^.*\\d$")){
            return true;
        }
        return false;
    }

    private boolean validateValue(String value, String key) {
        if (key.endsWith(".type")){
            return value.equals("concatenation");
        }
        if (key.contains(".source.") && key.matches("^.*\\d$") ){
            return patternBgbt.matcher(value).matches() && validateBTsBGs(value);
        }
        if (key.endsWith(".expression")){
            return value.matches("%1( |-)%2(( |-)%[0-9])*");
        }

        if (key.endsWith(".target")) {
            return patternXml.matcher(value).matches() && validateXpath(value);
        }
        return false;
    }

    private boolean validateXpath(String value) {
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
