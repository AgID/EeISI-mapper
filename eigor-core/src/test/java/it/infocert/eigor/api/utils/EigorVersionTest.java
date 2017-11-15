package it.infocert.eigor.api.utils;

import com.google.common.io.Resources;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EigorVersionTest {

    @Test
    public void gitDotPropertiesFileExists() throws Exception {
        assertNotNull(Resources.getResource("git.properties"));
    }

    @Test
    public void shouldContainMandatoryFields() throws Exception {
        Map<String, String> versionInfoMap = EigorVersion.getAsMap();
        assertTrue(versionInfoMap.containsKey("git.build.version"));
        assertTrue(versionInfoMap.containsKey("git.branch"));
        assertTrue(versionInfoMap.containsKey("git.commit.id"));
        assertTrue(versionInfoMap.containsKey("git.commit.time"));
        assertTrue(versionInfoMap.containsKey("git.dirty"));
    }

}