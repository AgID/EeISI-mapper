package it.infocert.eigor.api.mapping.toCen;

import org.junit.Before;
import org.reflections.Reflections;

public class ManyCen2OneXpathMappingValidatorTest {

    private ManyCen2OneXpathMappingValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new ManyCen2OneXpathMappingValidator("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*", "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", new Reflections("it.infocert"));
    }
}
