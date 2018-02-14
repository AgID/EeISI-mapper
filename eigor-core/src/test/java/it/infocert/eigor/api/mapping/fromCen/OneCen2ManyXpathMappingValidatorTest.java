package it.infocert.eigor.api.mapping.fromCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.Before;
import org.junit.Test;

public class OneCen2ManyXpathMappingValidatorTest {

    private OneCen2ManyXpathMappingValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new OneCen2ManyXpathMappingValidator("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*", "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", new JavaReflections(), ErrorCode.Location.UBL_IN);
    }

    /**
     * mapping.MAPR-SP-11.type
     * mapping.MAPR-SP-11.cen.source
     * mapping.MAPR-SP-11.xml.target.1
     * mapping.MAPR-SP-11.xml.target.1.start
     * mapping.MAPR-SP-11.xml.target.1.end
     * mapping.MAPR-SP-11.xml.target.2
     * mapping.MAPR-SP-11.xml.target.2.start
     * @throws Exception
     */
    @Test
    public void shouldPassIfKeysAndValuesAreValid() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfTypeKeyValueDoesNotMatch() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "wrong");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfStartKeyValueDoesNotMatchNumber() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "aaa");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfEndKeyValueDoesNotMatchNumber() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "aaa");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfStartKeyMissingForTarget() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfSourceKeyMissing() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfTargetKeyMissing() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAValueDoesNotExistInCen() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT999999");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAValueDoesNotExistInXml() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "\\NOTXPATH");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingCenValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdPaese");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingXmlValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "split");
        mappings.put("mapping.MAPR-SP-11.cen.source", "/BG0004/BT0031");
        mappings.put("mapping.MAPR-SP-11.xml.target.1", "");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.start", "1");
        mappings.put("mapping.MAPR-SP-11.xml.target.1.end", "3");
        mappings.put("mapping.MAPR-SP-11.xml.target.2", "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/IdTrasmittente/IdCodice");
        mappings.put("mapping.MAPR-SP-11.xml.target.2.start", "3");

        validator.validate(mappings);
    }
}
