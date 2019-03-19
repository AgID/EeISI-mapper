package it.infocert.eigor.cli;

import it.infocert.eigor.cli.commands.ConversionCommand;
import it.infocert.eigor.cli.commands.ReportFailuereCommand;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JoptsimpleBasecCommandLineInterpreterTest {

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
    public void shouldReportAllSupportedFormatsInCaseUserProvideAnUnsupportedFormat() {
        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter();
        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand) (sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(), "--source", "aaa", "--target", "ooo", "--output", outputDir.getAbsolutePath()}));
        // then
        assertThat(cliCommand.getErrorMessage(), is(String.format("Source format 'aaa' is not supported. Please choose one among: %s.", sut.getSupportedSourceFormats())));
    }


    @Test
    public void shouldReportAllSupportedSourceFormatsInCaseUserProvideAnUnsupportedSourceFormat() {
        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter();
        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand) (sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "aaa",
                "--target", "ooo",
                "--output", outputDir.getAbsolutePath()}));
        // then
        assertThat(cliCommand.getErrorMessage(), is(String.format("Source format 'aaa' is not supported. Please choose one among: %s.", sut.getSupportedSourceFormats())));
    }

    @Test
    public void shouldAcceptForceParameter() {
        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter();
        // when
        ConversionCommand cliCommand = (ConversionCommand) sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fatturapa",
                "--target", "ubl",
                "--output", outputDir.getAbsolutePath(),
                "--force"});
        // then
        assertThat(cliCommand.isForceConversion(), is(true));
    }

    @Test
    public void shouldNotForceConversionByDefault() {
        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter();
        // when
        ConversionCommand cliCommand = (ConversionCommand) sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "fatturapa",
                "--target", "ubl",
                "--output", outputDir.getAbsolutePath()});
        // then
        assertThat(cliCommand.isForceConversion(), is(false));
    }
}
