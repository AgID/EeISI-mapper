package it.infocert.eigor.api.mapping;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InvoiceCenXpathMappingValidator;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.assertFalse;

public class InputInvoiceXpathMapTest {

    private InputInvoiceXpathMap xpathMap;

    @Before
    public void setUp() throws Exception {
        xpathMap = new InputInvoiceXpathMap(new InvoiceCenXpathMappingValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?", new Reflections("it.infocert")));
    }

    @Test
    public void invoicePathsShouldNotHaveEmptyValues() throws Exception {

        Multimap<String, String> mapping = xpathMap.getMapping("../eigor-test/src/main/resources/test-paths.properties");
        for (String path : mapping.keySet()) {
            assertFalse(mapping.get(path).isEmpty());
        }
    }

    @Test(expected = RuntimeException.class)
    public void mappingShouldBeClearWhenInvalidPath() {

        Multimap<String, String> mapping = xpathMap.getMapping("/tmp/fake.properties");
        assertFalse(mapping.isEmpty());
    }

}