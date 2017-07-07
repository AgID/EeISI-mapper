package it.infocert.eigor.api.mapping.toCen;


import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.mapping.InvoiceMappingValidator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class OneCen2ManyXpathMappingValidator implements InvoiceMappingValidator{
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
