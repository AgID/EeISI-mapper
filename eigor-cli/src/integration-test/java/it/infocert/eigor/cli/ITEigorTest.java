package it.infocert.eigor.cli;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static it.infocert.eigor.test.Files.findFirstFileByNameOrNull;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ITEigorTest {

    public @Rule
    TemporaryFolder tmp = new TemporaryFolder();
    public @Rule
    TestName test = new TestName();
    private File outputDir;
    private ByteArrayOutputStream err;
    private File plainFattPa;
    private CommandLineInterpreter cli;

    @Before
    public void setUpCommandLineInterpeter() {
        cli = new JoptsimpleBasecCommandLineInterpreter();
    }

    @Before
    public void redirectConsoleStreams() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
    }

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
    public void failWhenOutputIsMissing() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fake",
                "--target", "fake"
        });
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("output"),
                containsString("missing")
        ));
    }

    @Test
    public void checkConversionFilesExistence() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fatturapa",
                "--target", "ubl",
                "--output", outputDir.getAbsolutePath()
        });
        // then
        List<File> files = asList(outputDir.listFiles());
        assertThat("converted invoice, cen invoice, rule report, log expected, got: " + files, files, hasSize(8));
        assertThat(files + " found", findFirstFileByNameOrNull(outputDir, "invoice-cen.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputDir, "invoice-target.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputDir, "rule-report.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputDir, "invoice-transformation.log"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputDir, "invoice-source.xml"), notNullValue());
    }

    @Test
    public void failWhenTargetIsMissing() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--source", "xmlcen"
        });
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("target"),
                containsString("missing")
        ));
    }

    private String err() {
        return new String(err.toByteArray());
    }
}
