package it.infocert.eigor.api.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class CopierTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    
    @Test
    public void justShowHowItIsSupposedToBeUsed() throws IOException {
        File dest = tmp.newFolder("dest");
        new Copier(dest).copyFromClasspath("/it/infocert/eigor/api/io");
    }

    @Test
    public void shouldCopyAcontentTakenFromAJar() throws IOException {
        File dest = tmp.newFolder("dest");
        new Copier(dest).copyFromJar("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/");
        System.out.println(Arrays.asList( dest.listFiles() ));
    }

}
