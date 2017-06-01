package it.infocert.eigor.converter.ubl2cen.mapping;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceXpathMap;
import it.infocert.eigor.converter.ubl2cen.Ubl2Cen;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UblXpathMapTest {

    private InputInvoiceXpathMap ublXpathMap;

    @Before
    public void setUp() throws Exception {
        ublXpathMap = new InputInvoiceXpathMap();
    }

    @Test
    public void invoicePathsShouldNotHaveEmptyValues() throws Exception {

        Multimap<String, String> mapping = ublXpathMap.getMapping(Ubl2Cen.MAPPING_PATH);

        for (String path : mapping.keySet()) {
            assertFalse(mapping.get(path).isEmpty());
        }
    }

    @Test(expected = RuntimeException.class)
    public void mappingShouldBeClearWhenInvalidPath() {

        Multimap<String, String> mapping = ublXpathMap.getMapping("/tmp/fake.properties");
        assertFalse(mapping.isEmpty());
    }

}