package it.infocert.eigor.cli;

import it.infocert.eigor.api.*;
import it.infocert.eigor.cli.commands.ConversionCommand;
import it.infocert.eigor.cli.commands.ReportFailuereCommand;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class JoptsimpleBasecCommandLineInterpreterTest {

    @Mock
    ToCenConversionRepository toCenRepo;
    @Mock
    FromCenConversionRepository fromCenRepo;
    @Mock
    RuleRepository ruleRepository;

    public @Rule
    TemporaryFolder tmp = new TemporaryFolder();
    public @Rule
    TestName test = new TestName();

    File inputDir;
    File outputDir;
    File plainFattPa;


    @Before
    public void setUpFolders() throws IOException {

        //...an "input" folder where input file can be stored.
        inputDir = tmp.newFolder(test.getMethodName(), "input");

        //...an "output" folder where output files can be stored
        outputDir = tmp.newFolder(test.getMethodName(), "output");

        //...let's copy an input invoice in the input folder
        plainFattPa = TestUtils.copyResourceToFolder("/examples/fattpa/fatt-pa-plain-vanilla.xml", inputDir);

    }

    @Test
    public void shouldReportAllSupportedFormatsInCaseUserProvideAnUnsupportedFormat() throws Exception {

        // given
        given( fromCenRepo.findConversionFromCen(anyString()) ).willReturn(null);
        given( fromCenRepo.supportedFromCenFormats() ).willReturn( new LinkedHashSet<>(Arrays.asList("frm1", "frm2") ) );
        given( toCenRepo.findConversionToCen(anyString()) ).willReturn(mock(ToCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, ruleRepository);

        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand)(sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(), "--source", "aaa", "--target", "ooo", "--output", outputDir.getAbsolutePath()}));

        // then
        assertThat( cliCommand.getErrorMessage(), is("Target formatPadded 'ooo' is not supported. Please choose one among: [frm1, frm2].") );

    }


    @Test
    public void shouldReportAllSupportedSourceFormatsInCaseUserProvideAnUnsupportedSourceFormat() throws Exception {

        // given
        given( toCenRepo.findConversionToCen(anyString()) ).willReturn(null);
        given( toCenRepo.supportedToCenFormats() ).willReturn( new LinkedHashSet<>(Arrays.asList("src1", "src2") ) );

        given( fromCenRepo.findConversionFromCen(anyString()) ).willReturn(mock(FromCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, ruleRepository);

        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand)(sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "aaa",
                "--target", "ooo",
                "--output", outputDir.getAbsolutePath()}));

        // then
        assertThat( cliCommand.getErrorMessage(), is("Source formatPadded 'aaa' is not supported. Please choose one among: [src1, src2].") );

    }

    @Test
    public void shouldAcceptForceParameter() throws Exception {

        // given
        given( toCenRepo.findConversionToCen(anyString()) ).willReturn(mock(ToCenConversion.class));
        given( fromCenRepo.findConversionFromCen(anyString()) ).willReturn(mock(FromCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, ruleRepository);

        // when
        ConversionCommand cliCommand = (ConversionCommand) sut.parseCommandLine(new String[] { "--input", plainFattPa.getAbsolutePath(), "--source", "aaa", "--target", "ooo", "--output", outputDir.getAbsolutePath(), "--force" });;

        // then
        assertThat( cliCommand.isForceConversion(), is(true) );

    }

    @Test
    public void shouldNotForceConversionByDefault() throws Exception {

        // given
        given( toCenRepo.findConversionToCen(anyString()) ).willReturn(mock(ToCenConversion.class));
        given( fromCenRepo.findConversionFromCen(anyString()) ).willReturn(mock(FromCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, ruleRepository);

        // when
        ConversionCommand cliCommand = (ConversionCommand) sut.parseCommandLine(new String[] { "--input", plainFattPa.getAbsolutePath(), "--source", "aaa", "--target", "ooo", "--output", outputDir.getAbsolutePath() });;

        // then
        assertThat( cliCommand.isForceConversion(), is(false) );

    }

}