package it.infocert.eigor.cli;

import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
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
import java.util.List;
import java.util.Properties;

import static it.infocert.eigor.test.Files.findFirstFileByNameOrNull;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ITEigorTest {

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
        Properties properties = new Properties();
        ReflectionBasedRepository genericRepo = new ReflectionBasedRepository(reflections);
        IntegrityRulesRepository integrityRepo = new IntegrityRulesRepository(properties);
        cli = new JoptsimpleBasecCommandLineInterpreter(
                genericRepo, genericRepo, integrityRepo
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
        plainFattPa = TestUtils.copyResourceToFolder("/examples/fattpa/fatt-pa-plain-vanilla.xml", inputDir);

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

        assertThat( files + " found", findFirstFileByNameOrNull(outputDir, "invoice-cen.csv"), notNullValue() );
        assertThat( files + " found", findFirstFileByNameOrNull(outputDir,"invoice-target.fake"), notNullValue() );
        assertThat( files + " found", findFirstFileByNameOrNull(outputDir, "rule-report.csv"), notNullValue() );
        assertThat( files + " found", findFirstFileByNameOrNull(outputDir, "invoice-transformation.log"), notNullValue() );
        assertThat( files + " found", findFirstFileByNameOrNull(outputDir, "invoice-source.xml"), notNullValue() );

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

    private String err() {
        return new String(err.toByteArray());
    }

    private String out() {
        return new String(out.toByteArray());
    }


}