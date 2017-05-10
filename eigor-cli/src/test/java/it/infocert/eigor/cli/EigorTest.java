package it.infocert.eigor.cli;

import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.rules.repositories.ConstraintsRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.reflections.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class EigorTest {

    public @Rule TemporaryFolder tmp = new TemporaryFolder();
    public @Rule TestName test = new TestName();
    private File inputDir;
    private File outputDir;
    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private File plainFattPa;
    private CommandLineInterpreter cli;

    @Before public void setUpCommandLineInterpeter() {
        Reflections reflections = new Reflections("it.infocert");
        ReflectionBasedRepository genericRepo = new ReflectionBasedRepository(reflections);
        ConstraintsRepository constraintsRepo = new ConstraintsRepository(reflections);
        cli = new JoptsimpleBasecCommandLineInterpreter(
                genericRepo, genericRepo, constraintsRepo
        );
    }

    @Before public void redirectConsoleStreams() {
        out = new ByteArrayOutputStream();
        System.setOut( new PrintStream(out));

        err = new ByteArrayOutputStream();
        System.setErr( new PrintStream(err));
    }

    @Before public void setUpFolders() throws IOException {

        //...an "input" folder where input file can be stored.
        inputDir = tmp.newFolder(test.getMethodName(), "input");

        //...an "output" folder where output files can be stored
        outputDir = tmp.newFolder(test.getMethodName(), "output");

        //...let's copy an input invoice in the input folder
        plainFattPa = TestUtils.copyResourceToFolder("/fatt-pa-plain-vanilla.xml", inputDir);

    }




    @Test public void printHelpWhenLaunchedWithoutArguments() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{} );

        // then
        assertThat(out().toLowerCase(), allOf(
                containsString("--target"),
                containsString("--source"),
                containsString("--input"),
                containsString("--output")
        ));

    }


    @Test public void failWhenTargetIsMissing() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--source", "fake"
        } );

        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("target"),
                containsString("missing")
        ));

    }


    @Test public void failWhenSourceIsMissing() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--target", "fake"
        } );

        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("source"),
                containsString("missing")
        ));

    }

    @Test public void failWhenOutputIsMissing() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fake",
                "--target", "fake"
        } );

        // then
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("output"),
                containsString("missing")
        ));

    }

    @Test public void failWhenInputIsMissing() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--source", "fake",
                "--target", "fake",
                "--output", outputDir.getAbsolutePath()
        } );

        // then
        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("input"),
                containsString("missing")
        ));

    }

    @Test public void executeWithFakeTransformations() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fake",
                "--target", "fake",
                "--output", outputDir.getAbsolutePath()
        } );

        // then
        List<File> files = asList( outputDir.listFiles() );
        assertThat( "converted invoice, cen invoice, rule report, log expected, got: " + files, files, hasSize(5) );

        assertThat( files + " found", findFirstFileOrNull(outputDir, f -> f.getName().equals("invoice-cen.csv")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputDir, f -> f.getName().equals("invoice-target.fake")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputDir, f -> f.getName().equals("rule-report.csv")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputDir, f -> f.getName().equals("invoice-transformation.log")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputDir, f -> f.getName().equals("invoice-source.xml")), notNullValue() );

    }

    private File findFirstFileOrNull(File outputDir, Predicate<File> col) {
        return Arrays.stream(outputDir.listFiles()).filter(col).findFirst().orElse(null);
    }

    @Test public void failWhenInputDoesNotExist() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", "i-bet-this-file-does-not-exist.xml",
                "--source", "fattpa1.2",
                "--target", "ubl",
                "--output", outputDir.getAbsolutePath()
        } );

        // then
        assertThat(err().toLowerCase(), allOf(
                containsString("input invoice"),
                containsString("i-bet-this-file-does-not-exist.xml"),
                containsString("does not exist")
        ));

    }

    @Test public void failWhenOutputDoesNotExist() throws IOException {

        // when
        new EigorCli(cli).run(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fattpa1.2",
                "--target", "ubl",
                "--output", "i-bet-this-folder-does-not-exist"
        } );

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