package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConversionCommandTest {

    @Mock
    ToCenConversion toCen;
    @Mock FromCenConversion fromCen;
    @Mock RuleRepository ruleRepository;

    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();
    private File outputFolderFile;

    @Before
    public void setUpMocks() throws IOException, SyntaxErrorInInvoiceFormatException {
        outputFolderFile = tmpRule.newFolder("output");
        ConversionResult t = new ConversionResult("result".getBytes());
        when(fromCen.convert(any())).thenReturn(t);
        when(toCen.convert(any())).thenReturn(new BG0000Invoice());
    }

    @Test
    public void shouldUseTheExtensionSpecifiedFromTheFromCenConverterAsOutputExtension() throws Exception {

        // given
        given( fromCen.extension() ).willReturn(".json");

        Path inputInvoice = FileSystems.getDefault().getPath("logs", "access.log");
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceSourceFormat = null;
        ConversionCommand sut = new ConversionCommand(ruleRepository, toCen, fromCen, inputInvoice, outputFolder, invoiceSourceFormat);
        PrintStream err = new PrintStream( new ByteArrayOutputStream() );
        PrintStream out = new PrintStream( new ByteArrayOutputStream() );

        // when
        sut.execute(out, err);

        // then
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-target.json") );
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-cen.csv") );
        assertThat(asList(outputFolderFile.list()), hasItem("rule-report.csv") );
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-transformation.log") );

    }

}