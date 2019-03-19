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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class EigorTest {

    public @Rule
    TemporaryFolder tmp = new TemporaryFolder();
    public @Rule
    TestName test = new TestName();
    private File outputDir;
    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private File plainFattPa;
    private CommandLineInterpreter cli;

    @Before
    public void setUpCommandLineInterpeter() {
        cli = new JoptsimpleBasecCommandLineInterpreter();
    }

    @Before
    public void redirectConsoleStreams() {
        out = new ByteArrayOutputStream();
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
        plainFattPa = it.infocert.eigor.cli.TestUtils.copyResourceToFolder("/examples/fattpa/fatt-pa-plain-vanilla.xml", inputDir);
    }


    @Test
    public void printHelpWhenLaunchedWithoutArguments() {
        // when
        new EigorCli(cli).run(new String[]{});
        // then
        assertThat(out().toLowerCase(), allOf(
                containsString("--target"),
                containsString("--source"),
                containsString("--input"),
                containsString("--output")
        ));
    }

    @Test
    public void failWhenSourceIsMissing() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--target", "fake"
        });
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("source"),
                containsString("missing")
        ));
    }

    @Test
    public void failWhenInputIsMissing() {
        // when
        new EigorCli(cli).run(new String[]{
                "--source", "fake",
                "--target", "fake",
                "--output", outputDir.getAbsolutePath()
        });
        // then
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("input"),
                containsString("missing")
        ));
    }

    @Test
    public void failWhenInputDoesNotExist() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", "i-bet-this-file-does-not-exist.xml",
                "--source", "fattpa1.2",
                "--target", "ubl",
                "--output", outputDir.getAbsolutePath()
        });
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("input invoice"),
                containsString("i-bet-this-file-does-not-exist.xml"),
                containsString("does not exist")
        ));
    }

    @Test
    public void failWhenOutputDoesNotExist() {
        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fattpa1.2",
                "--target", "ubl",
                "--output", "i-bet-this-folder-does-not-exist"
        });
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("output folder"),
                containsString("i-bet-this-folder-does-not-exist"),
                containsString("does not exist")
        ));
    }

    private String err() {
        return new String(err.toByteArray());
    }

    private String out() {
        return new String(out.toByteArray());
    }
}
