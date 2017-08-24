package it.infocert.eigor.converter.cen2fattpa;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResourceTest {

    @Test
    public void shouldLoadResourcesTheClassicalWay(){

        InputStream resourceAsStream = getClass().getResourceAsStream("/resourcetest.txt");
        assertNotNull( resourceAsStream );
    }

    @Test
    public void shouldLoadTheMappingFileTheClassicalWay(){

        InputStream resourceAsStream = getClass().getResourceAsStream("/converterdata/converter-cen-fattpa/mappings/one_to_one.properties");
        assertNotNull( resourceAsStream );
    }

    @Test
    public void shouldLoadTheMappingFileWithoutLeadingSlash(){

        Resource resource = new DefaultResourceLoader(ClassLoader.getSystemClassLoader()).getResource("classpath:converterdata/converter-cen-fattpa/mappings/one_to_one.properties");
        assertTrue( resource.exists() );
    }

    @Test
    public void shouldLoadTheMappingFileWithoutProtocol(){

        Resource resource = new DefaultResourceLoader().getResource("/converterdata/converter-cen-fattpa/mappings/one_to_one.properties");
        assertTrue( resource.exists() );
    }

}
