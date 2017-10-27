package it.infocert.eigor.api.mapping.fromCen;


import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.regex.Pattern;

public class OneCen2ManyXpathMappingValidator implements InvoiceMappingValidator{

    private final Pattern patternXml;
    private final Pattern patternBgbt;
    private final InvoiceUtils invoiceUtils;

    public OneCen2ManyXpathMappingValidator(String keyRegexXml, String keyRegexBgbt, Reflections reflections) {
        patternXml = Pattern.compile(keyRegexXml);
        patternBgbt = Pattern.compile(keyRegexBgbt);
        invoiceUtils = new InvoiceUtils(reflections);
    }

    @Override
    public void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException {
        HashMap<String, Boolean> startKeyExist = new HashMap<>();
        HashMap<String, String> mappingSource = new HashMap<>();
        HashMap<String, String> mappingTarget = new HashMap<>();
        for (String key : map.keySet()) {
            if (key.contains(".xml.target") && key.matches("^.*\\d$")) {
                if (!startKeyExist.containsKey(key)) {
                    startKeyExist.put(key, false);
                }
                int idx = key.indexOf(".xml.target");
                mappingTarget.put(key.substring(0,idx).concat(".type"), key);
            }
            if (key.endsWith(".start")) {
                startKeyExist.put(key.substring(0, key.length()-6), true);
            }
            if (key.endsWith(".type")){
                if (!mappingSource.containsKey(key)) {
                    mappingSource.put(key, null);
                }
                if (!mappingTarget.containsKey(key)) {
                    mappingTarget.put(key, null);
                }
            }
            if (key.endsWith(".cen.source")){
                int idx = key.indexOf(".cen.source");
                mappingSource.put(key.substring(0,idx).concat(".type"), key);
            }
            if (validateKey(key)) {
                for (String value : map.get(key)) {
                    if (!validateValue(value, key)) {
                        throw new SyntaxErrorInMappingFileException("Bad mapping value for key: " + key + " - " + value);
                    }
                }
            } else throw new SyntaxErrorInMappingFileException("Bad mapping key: " + key);
        }
        for (String key: startKeyExist.keySet()){
            if (key != null && startKeyExist.get(key) == null){
                throw new SyntaxErrorInMappingFileException("Missign start key for target: " + key);
            }
        }
        for (String key: mappingSource.keySet()){
            if (key != null && mappingSource.get(key) == null){
                throw new SyntaxErrorInMappingFileException("Missign source key for mapping: " + key);
            }
        }
        for (String key: mappingTarget.keySet()){
            if (key != null && mappingTarget.get(key) == null){
                throw new SyntaxErrorInMappingFileException("Missign target key for mapping: " + key);
            }
        }
    }

    /**
     * mapping.MAPR-SP-11.type
     * mapping.MAPR-SP-11.cen.source
     * mapping.MAPR-SP-11.xml.target.1
     * mapping.MAPR-SP-11.xml.target.1.start
     * mapping.MAPR-SP-11.xml.target.1.end
     * mapping.MAPR-SP-11.xml.target.2
     * mapping.MAPR-SP-11.xml.target.2.start
     * @param key
     * @return
     */
    private boolean validateKey(String key) {
        if (key.endsWith("type") ||
                key.endsWith("cen.source")) {
            return true;
        }
        if (key.contains(".xml.target.")){
            if (key.endsWith(".start") ||
                    key.endsWith(".end") ||
                    key.matches("^.*\\d$")){
                return true;
            }
        }
        return false;
    }

    /**
     * mapping.MAPR-SP-11.type=split
     mapping.MAPR-SP-11.cen.source=/BG0004/BT0031
     mapping.MAPR-SP-11.xml.target.1=/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese
     mapping.MAPR-SP-11.xml.target.1.start=1
     mapping.MAPR-SP-11.xml.target.1.end=3
     mapping.MAPR-SP-11.xml.target.2=/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice
     mapping.MAPR-SP-11.xml.target.2.start=3
     * @param value
     * @return
     */
    private boolean validateValue(String value, String key) {
        if (key.endsWith(".type")){
            return value.equals("split");
        }
        if (key.endsWith(".cen.source")){
            return patternBgbt.matcher(value).matches() && validateBTsBGs(value);
        }
        if (key.endsWith(".start") || key.endsWith(".end")){
            return value.matches("^[0-9]+$");
        }

        if (key.contains(".xml.target") && key.matches("^.*\\d$")) {
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
