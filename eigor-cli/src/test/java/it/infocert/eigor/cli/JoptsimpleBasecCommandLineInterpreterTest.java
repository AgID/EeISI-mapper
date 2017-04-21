package it.infocert.eigor.cli;

import it.infocert.eigor.api.*;
import it.infocert.eigor.cli.commands.ReportFailuereCommand;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class JoptsimpleBasecCommandLineInterpreterTest {

    @Mock
    ToCenConversionRepository toCenRepo;
    @Mock
    FromCenConversionRepository fromCenRepo;
    @Mock
    RuleRepository repo3;

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
        plainFattPa = TestUtils.copyResourceToFolder("/fatt-pa-plain-vanilla.xml", inputDir);

    }

    @Test
    public void shouldReportAllSupportedFormatsInCaseUserProvideAnUnsupportedFormat() throws Exception {

        // given
        given( fromCenRepo.findConversionFromCen(Mockito.anyString()) ).willReturn(null);
        given( fromCenRepo.supportedFormats() ).willReturn( new LinkedHashSet<>(Arrays.asList("frm1", "frm2") ) );
        given( toCenRepo.findConversionToCen(Mockito.anyString()) ).willReturn(mock(ToCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, repo3);

        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand)(sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(), "--source", "aaa", "--target", "ooo", "--output", outputDir.getAbsolutePath()}));

        // then
        assertThat( cliCommand.getErrorMessage(), is("Target format 'ooo' is not supported. Please choose one among: [frm1, frm2].") );

    }


    @Test
    public void shouldReportAllSupportedSourceFormatsInCaseUserProvideAnUnsupportedSourceFormat() throws Exception {

        // given
        given( toCenRepo.findConversionToCen(Mockito.anyString()) ).willReturn(null);
        given( toCenRepo.supportedToCenFormats() ).willReturn( new LinkedHashSet<>(Arrays.asList("src1", "src2") ) );

        given( fromCenRepo.findConversionFromCen(Mockito.anyString()) ).willReturn(mock(FromCenConversion.class));

        JoptsimpleBasecCommandLineInterpreter sut = new JoptsimpleBasecCommandLineInterpreter(toCenRepo, fromCenRepo, repo3);

        // when
        ReportFailuereCommand cliCommand = (ReportFailuereCommand)(sut.parseCommandLine(new String[]{
                "--input", plainFattPa.getAbsolutePath(),
                "--source", "aaa",
                "--target", "ooo",
                "--output", outputDir.getAbsolutePath()}));

        // then
        assertThat( cliCommand.getErrorMessage(), is("Source format 'aaa' is not supported. Please choose one among: [src1, src2].") );

    }

}