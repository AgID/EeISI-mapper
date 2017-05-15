package it.infocert.eigor.converter.ubl2cen.mapping;

import com.google.common.collect.Multimap;
import org.junit.Test;

import static org.junit.Assert.*;

public class UblXpathMapTest {

    //TODO update to check for all mandatory paths when converter is finished
    private String mandatoryInvoicePaths[] = {
            "/BT0001",
            "/BT0002",
            "/BT0003",
            "/BT0005",
            "/BT0031",
            "/BT0040"};


    @Test
    public void getMappingShouldContainAtleastMandatoryInvoicePaths() throws Exception {

        UblXpathMap ublXpathMap = new UblXpathMap();
        Multimap<String, String> mapping = ublXpathMap.getMapping();

        for (String path : mandatoryInvoicePaths) {
            assertTrue(mapping.containsKey(path));
        }
    }

    @Test
    public void getMappingMandatoryInvoicePathsShouldNotHaveEmptyValues() throws Exception {

        UblXpathMap ublXpathMap = new UblXpathMap();
        Multimap<String, String> mapping = ublXpathMap.getMapping();

        for (String path : mandatoryInvoicePaths) {
            assertFalse(mapping.get(path).isEmpty());

        }
    }

}