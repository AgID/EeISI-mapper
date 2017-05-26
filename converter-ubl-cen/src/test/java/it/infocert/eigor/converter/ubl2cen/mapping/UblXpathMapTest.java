package it.infocert.eigor.converter.ubl2cen.mapping;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class UblXpathMapTest {

    private UblXpathMap ublXpathMap;

    @Before
    public void setUp() throws Exception {
        ublXpathMap = new UblXpathMap();
    }

    @Test
    public void invoicePathsShouldNotHaveEmptyValues() throws Exception {

        Multimap<String, String> mapping = ublXpathMap.getMapping();

        for (String path : mapping.keySet()) {
            assertFalse(mapping.get(path).isEmpty());
        }
    }

}