package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceXpathMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

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