package it.infocert.eigor.api.mapping.fromCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;


public class InvoiceXpathCenMappingValidatorTest {

    private InvoiceXpathCenMappingValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new InvoiceXpathCenMappingValidator("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*", new Reflections("it.infocert"));
    }

    @Test
    public void shouldPassIfKeysAndXpathsAreValid() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune", "/BG-7/BG-8/BT-52");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP", "/BG-7/BG-8/BT-53");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia", "/BG-7/BG-8/BT-54");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione", "/BG-7/BG-8/BT-55");

        validator.validate(mappings);
    }






    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAKeyValueDoesNotMatchRegex() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune", "/BG-7/BG-8/BT-52");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP", "/BG-7/BG-8/BT-53");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia", "/BG-7/BG-8/BT-54");

        mappings.put("/FacturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione", "/BG-7/BG-8/BT-55");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAValueDoesNotExistInCen() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia", "/BG-9999");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune", "/BG-7/BG-8/BT-52");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP", "/BG-7/BG-8/BT-53");
        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia", "/BG-7/BG-8/BT-54");

        mappings.put("/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione", "");

        validator.validate(mappings);
    }
}