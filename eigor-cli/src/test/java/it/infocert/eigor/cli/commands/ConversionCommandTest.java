package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static it.infocert.eigor.test.Files.findFirstFileByNameOrNull;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConversionCommandTest {

    @Mock
    ToCenConversion toCen;
    @Mock
    FromCenConversion fromCen;
    @Mock
    RuleRepository ruleRepository;

    static EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();

    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();

    File outputFolderFile;
    Path inputInvoice;
    InputStream invoiceSourceFormat;

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
        BinaryConversionResult t = new BinaryConversionResult("result".getBytes());
        when(fromCen.convert(any(BG0000Invoice.class))).thenReturn(t);
        when(toCen.convert(any(InputStream.class))).thenReturn(
                new ConversionResult<>(new BG0000Invoice())
        );
    }

    @Before
    public void setUpInvoiceSourceFormat() {
        invoiceSourceFormat = new ByteArrayInputStream("<invoice>the-invoice</invoice>".getBytes());
    }

    @Test
    public void shouldUseTheExtensionSpecifiedFromTheFromCenConverterAsOutputExtension() throws Exception {

        // given
        given(fromCen.extension()).willReturn(".json");

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceInSourceFormat = new ByteArrayInputStream( "<invoice>invoice</invoice>".getBytes() );

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder().setRuleRepository(ruleRepository).setToCen(toCen).setFromCen(fromCen).setInputInvoice(inputInvoice).setOutputFolder(outputFolder).setInvoiceInSourceFormat(invoiceInSourceFormat).setForceConversion(false).setConfiguration(configuration).setRunIntermediateValidation(false).build();

        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());

        // when
        sut.execute(out, err);

        // then
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-source.txt"));
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-target.json"));
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-cen.csv"));
        assertThat(asList(outputFolderFile.list()), hasItem("rule-report.csv"));
//        assertThat(asList(outputFolderFile.list()), hasItem("invoice-transformation.log"));

    }

    @Test
    public void fromCenConversionShouldCreateCsvIfConversionResultHasErrorsAndForceFlag() throws Exception {

        // given
        given(fromCen.extension()).willReturn(".xml");

        List<IConversionIssue> myErrors = Arrays.asList(
                (IConversionIssue)ConversionIssue.newError(new EigorRuntimeException(
                        "test exception",
                        ErrorCode.Location.FATTPA_OUT,
                        ErrorCode.Action.GENERIC,
                        ErrorCode.Error.INVALID
                ))
        );
        when(fromCen.convert(any(BG0000Invoice.class))).thenReturn(new BinaryConversionResult("bytes".getBytes(), myErrors));

        // when converting a mock invoice, issues should occur
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());


        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder().setRuleRepository(ruleRepository).setToCen(toCen).setFromCen(fromCen).setInputInvoice(inputInvoice).setOutputFolder(outputFolder).setInvoiceInSourceFormat(invoiceSourceFormat).setForceConversion(true).setConfiguration(configuration).setRunIntermediateValidation(false).build();
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        // when
        sut.execute(out, err);

        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());

        assertTrue(outputFolderFile.exists());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-source.txt"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-cen.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-target.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "rule-report.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());

        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "fromcen-errors.csv"), notNullValue());

    }

    @Test
    public void toCenConversionShouldCreateCsvIfConversionResultHasErrors() throws IOException, SyntaxErrorInInvoiceFormatException {


        List<IConversionIssue> myErrors = Arrays.asList(
                (IConversionIssue)ConversionIssue.newError(new EigorRuntimeException(
                        "test exception",
                        ErrorCode.Location.FATTPA_OUT,
                        ErrorCode.Action.GENERIC,
                        ErrorCode.Error.INVALID
                ))
        );
        when(toCen.convert(any(InputStream.class))).thenReturn(new ConversionResult(myErrors, new BG0000Invoice()));

        // given
        given(fromCen.extension()).willReturn(".xml");

        // when converting a mock invoice, issues should occur
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder().setRuleRepository(ruleRepository).setToCen(toCen).setFromCen(fromCen).setInputInvoice(inputInvoice).setOutputFolder(outputFolder).setInvoiceInSourceFormat(invoiceSourceFormat).setForceConversion(false).setConfiguration(configuration).setRunIntermediateValidation(false).build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());

        // when
        sut.execute(out, err);

        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());

        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "tocen-errors.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());
    }

    @Test
    public void toCenConversionShouldCreateCsvAndAllFilesIfConversionResultHasErrorsButForceIsTrue() throws IOException, SyntaxErrorInInvoiceFormatException {


        List<IConversionIssue> myErrors = Arrays.asList(
                (IConversionIssue) ConversionIssue.newError(new EigorRuntimeException(
                        "test exception",
                        ErrorCode.Location.FATTPA_OUT,
                        ErrorCode.Action.GENERIC,
                        ErrorCode.Error.INVALID
                )));
        when(toCen.convert(any(InputStream.class))).thenReturn(new ConversionResult(myErrors, new BG0000Invoice()));

        // given
        given(fromCen.extension()).willReturn(".xml");

        // when converting a mock invoice, issues should occur
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder().setRuleRepository(ruleRepository).setToCen(toCen).setFromCen(fromCen).setInputInvoice(inputInvoice).setOutputFolder(outputFolder).setInvoiceInSourceFormat(invoiceSourceFormat).setForceConversion(true).setConfiguration(configuration).setRunIntermediateValidation(false).build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());

        // when
        sut.execute(out, err);

        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());

        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "tocen-errors.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-source.txt"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-cen.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-target.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "rule-report.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());
    }

}
