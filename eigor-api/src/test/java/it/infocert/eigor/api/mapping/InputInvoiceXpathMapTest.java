package it.infocert.eigor.api.mapping;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.toCen.InvoiceCenXpathMappingValidator;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InputInvoiceXpathMapTest {

    private InputInvoiceXpathMap xpathMap;

    @Before
    public void setUp() throws Exception {
        xpathMap = new InputInvoiceXpathMap(new InvoiceCenXpathMappingValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?", new Reflections("it.infocert")));
    }

    @Test
    public void shouldLoadConfigurationFromResource() throws Exception {

        Resource mappingFile = new DefaultResourceLoader().getResource("file:../eigor-test/src/main/resources/test-paths.properties");
        assertTrue(mappingFile.getFile().getAbsolutePath() + " does not exist.", mappingFile.exists());

        Multimap<String, String> mapping = xpathMap.getMapping(mappingFile);
        assertAllValuesAreNotEmpty(mapping);
    }

    @Test
    public void shouldLoadConfigurationFromPath() throws Exception {
        Multimap<String, String> mapping = xpathMap.getMapping("../eigor-test/src/main/resources/test-paths.properties");
        assertAllValuesAreNotEmpty(mapping);
    }

    private void assertAllValuesAreNotEmpty(Multimap<String, String> mapping) {
        for (String path : mapping.keySet()) {
            assertFalse(mapping.get(path).isEmpty());
        }
    }

    @Test
    public void mappingShouldThrowExceptionWhenInvalidPath() {

        try{
            xpathMap.getMapping("/tmp/fake.properties");
            fail();
        }catch(RuntimeException re){

        }catch(Exception e){
            fail();
        }

    }

}