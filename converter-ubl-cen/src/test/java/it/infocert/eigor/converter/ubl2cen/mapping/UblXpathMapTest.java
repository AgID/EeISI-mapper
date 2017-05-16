package it.infocert.eigor.converter.ubl2cen.mapping;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class UblXpathMapTest {

    private UblXpathMap ublXpathMap;
    private List<String> invoicePaths;
    private List<String> italianPaths;

    @Before
    public void setUp() throws Exception {
        ublXpathMap = new UblXpathMap();
        invoicePaths = ublXpathMap.getInvoicePaths();
        italianPaths = ublXpathMap.getItalianPaths();
    }

    @Test
    public void getMappingShouldHaveSameSizeAsPaths() throws Exception {

        Multimap<String, String> mapping = ublXpathMap.getMapping();

        assertTrue(mapping.size() == invoicePaths.size());
        assertTrue(mapping.size() == italianPaths.size());
    }

    @Test
    public void getMappingShouldContainSameElementsAsPathsLists() throws Exception {

        Multimap<String, String> mapping = ublXpathMap.getMapping();
        for (int i = 0; i < invoicePaths.size(); i++) {
            String invoicePath = invoicePaths.get(i);
            String italianPath = italianPaths.get(i);
            Collection<String> mapInvoicePath = mapping.get(invoicePath);
            assertTrue(mapInvoicePath.contains(italianPath));
        }
    }

    @Test
    public void invoicePathsShouldNotHaveEmptyValues() throws Exception {

        Multimap<String, String> mapping = ublXpathMap.getMapping();

        for (String path : invoicePaths) {
            assertFalse(mapping.get(path).isEmpty());

        }
    }

}