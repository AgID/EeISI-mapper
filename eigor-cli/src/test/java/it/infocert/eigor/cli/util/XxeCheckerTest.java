package it.infocert.eigor.cli.util;

import it.infocert.eigor.api.SchematronValidator;
import it.infocert.eigor.cli.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class XxeCheckerTest {

    public @Rule
    TemporaryFolder tmp = new TemporaryFolder();
    public @Rule
    TestName test = new TestName();

    private File outputDir;
    private File plainFattPa;
    @Before
    public void setUpFolders() throws IOException {
        //...an "input" folder where input file can be stored.
        File inputDir = tmp.newFolder(test.getMethodName(), "input");
        //...an "output" folder where output files can be stored
        outputDir = tmp.newFolder(test.getMethodName(), "output");
        //...let's copy an input invoice in the input folder
        plainFattPa = TestUtils.copyResourceToFolder("/examples/fattpa/fatt-pa-plain-vanilla.xml", inputDir);
    }

    @Test
    public void PassXxeChecker() {

       Path inputInvoice = FileSystems.getDefault().getPath(plainFattPa.getAbsolutePath());
        assertTrue(XxeChecker.parser(inputInvoice) );

    }
}
