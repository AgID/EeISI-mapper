package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.cli.EigorCli;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPAConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConversionCommandTest {

    @Mock ToCenConversion toCen;
    @Mock FromCenConversion fromCen;
    @Mock RuleRepository ruleRepository;

    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();

    private File outputFolderFile;
    private Path inputInvoice;

    @Before
    public void setUpOutputFolder() throws IOException, SyntaxErrorInInvoiceFormatException {
        outputFolderFile = tmpRule.newFolder("output");
    }

    @Before
    public void setUpInitFolder() throws IOException, SyntaxErrorInInvoiceFormatException {
        File inputFolderFile = tmpRule.newFolder("input");

        // a generic mock invoice
        File file = new File(inputFolderFile, "invoice.txt");
        writeStringToFile(file, "mock invoice!");
        inputInvoice = file.toPath();
    }

    @Before
    public void setUpOutputMocks() throws IOException, SyntaxErrorInInvoiceFormatException {
        ConversionResult t = new ConversionResult("result".getBytes());
        when(fromCen.convert(any())).thenReturn(t);
        when(toCen.convert(any())).thenReturn(new BG0000Invoice());
    }

    @Test
    public void shouldUseTheExtensionSpecifiedFromTheFromCenConverterAsOutputExtension() throws Exception {

        // given
        given( fromCen.extension() ).willReturn(".json");

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceSourceFormat = null;
        ConversionCommand sut = new ConversionCommand(ruleRepository, toCen, fromCen, inputInvoice, outputFolder, invoiceSourceFormat);
        PrintStream err = new PrintStream( new ByteArrayOutputStream() );
        PrintStream out = new PrintStream( new ByteArrayOutputStream() );

        // when
        sut.execute(out, err);

        // then
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-source.txt") );
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-target.json") );
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-cen.csv") );
        assertThat(asList(outputFolderFile.list()), hasItem("rule-report.csv") );
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-transformation.log") );

    }

    @Test public void fromCen2FattPAConversionShouldCreateCsvIfConversionResultHasErrors() throws IOException {

        // given
        given( fromCen.extension() ).willReturn(".xml");

        // when converting a mock invoice, errors should occur
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceSourceFormat = null;
        ConversionCommand sut = new ConversionCommand(ruleRepository, toCen, new Cen2FattPAConverter(), inputInvoice, outputFolder, invoiceSourceFormat);
        PrintStream err = new PrintStream( new ByteArrayOutputStream() );
        PrintStream out = new PrintStream( new ByteArrayOutputStream() );

        // when
        sut.execute(out, err);

        // then a fromcen-errors.csv should be created for the errors along with the other files
        List<File> files = asList( outputFolderFile.listFiles() );

        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("invoice-source.txt")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("invoice-cen.csv")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("invoice-target.xml")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("rule-report.csv")), notNullValue() );
        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("invoice-transformation.log")), notNullValue() );

        assertThat( files + " found", findFirstFileOrNull(outputFolderFile, f -> f.getName().equals("fromcen-errors.csv")), notNullValue() );

    }

    private File findFirstFileOrNull(File outputDir, Predicate<File> col) {
        return Arrays.stream(outputDir.listFiles()).filter(col).findFirst().orElse(null);
    }


}