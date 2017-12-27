package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.utils.ReflectionsReflections;
import org.junit.Before;
import org.junit.Test;

public class ManyCen2OneXpathMappingValidatorTest {

    private ManyCen2OneXpathMappingValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new ManyCen2OneXpathMappingValidator("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*", "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", new ReflectionsReflections());
    }

    /**
     * mapping.1221_Indirizzo.type=concatenation
     * mapping.1221_Indirizzo.source.1=/BG0004/BG0005/BT0035
     * mapping.1221_Indirizzo.source.2=/BG0004/BG0005/BT0036
     * mapping.1221_Indirizzo.source.3=/BG0004/BG0005/BT0162
     * mapping.1221_Indirizzo.target=/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo
     * mapping.1221_Indirizzo.expression=%1 %2 %3
     * @throws Exception
     */
    @Test
    public void shouldPassIfKeysAndValuesAreValid() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test
    public void shouldPassIfLessSourceElemsThanInExpression() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfTypeKeyValueDoesNotMatch() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.MAPR-SP-11.type", "wrong");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfExprressionValueDoesNotMatchNumbers() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %A %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfSourceKeyValueDoesNotEndWithNumber() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.Z", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfExpressionKeyMissing() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfSourceKeyMissingFromExpression() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfTargetKeyMissing() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAValueDoesNotExistInCen() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT00999999999935");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAValueDoesNotExistInXml() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "\\NOTXPATH");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingCenValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingXmlValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("mapping.1221_Indirizzo.type", "concatenation");
        mappings.put("mapping.1221_Indirizzo.source.1", "/BG0004/BG0005/BT0035");
        mappings.put("mapping.1221_Indirizzo.source.2", "/BG0004/BG0005/BT0036");
        mappings.put("mapping.1221_Indirizzo.source.3", "/BG0004/BG0005/BT0162");
        mappings.put("mapping.1221_Indirizzo.target", "");
        mappings.put("mapping.1221_Indirizzo.expression", "%1 %2 %3");

        validator.validate(mappings);
    }
}
