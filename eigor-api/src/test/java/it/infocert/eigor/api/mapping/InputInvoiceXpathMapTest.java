package it.infocert.eigor.api.mapping;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceCenXpathMapValidator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class InputInvoiceXpathMapTest {

    private InputInvoiceXpathMap xpathMap;

    @Before
    public void setUp() throws Exception {
        xpathMap = new InputInvoiceXpathMap(new InputInvoiceCenXpathMapValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?"));
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