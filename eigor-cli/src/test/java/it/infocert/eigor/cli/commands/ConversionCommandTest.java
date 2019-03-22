package it.infocert.eigor.cli.commands;

import com.infocert.eigor.api.EigorApi;
import com.infocert.eigor.api.EigorApiBuilder;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.cli.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static it.infocert.eigor.test.Files.findFirstFileByNameOrNull;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConversionCommandTest {

    private String sourceFormat = "fatturapa";
    private String targetFormat = "ubl";

    private static EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();

    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();
    public @Rule
    TestName test = new TestName();

    private File outputFolderFile;
    private File plainFattPa;
    private InputStream invoiceInputStream;

    @Before
    public void setUpOutputFolder() throws IOException {
        //...an "input" folder where input file can be stored.
        File inputDir = tmpRule.newFolder(test.getMethodName(), "input");
        //...an "output" folder where output files can be stored
        outputFolderFile = tmpRule.newFolder("output");
        //...let's copy an input invoice in the input folder
        plainFattPa = TestUtils.copyResourceToFolder("/examples/fattpa/fatt-pa-plain-vanilla.xml", inputDir);
        invoiceInputStream = Files.newInputStream(plainFattPa.toPath(), READ);
    }

    private EigorApi api;

    @Before
    public void setUpApi() throws Exception {
        api = new EigorApiBuilder().build();
    }

    @Test
    public void shouldValidateIntermediateCenModel() throws IOException {

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInputStream)
                .setForceConversion(true)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .setApi(api)
                .setInvoiceInName(plainFattPa.getName())
                .setRunIntermediateValidation(true)
                .build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        // when
        sut.execute(out, err);
        // then

    }

    @Test
    public void shouldUseTheExtensionSpecifiedFromTheFromCenConverterAsOutputExtension() throws IOException {
        // From fatturapa have xml extension
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInputStream)
                .setForceConversion(false)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .setApi(api)
                .setInvoiceInName(plainFattPa.getName())
                .build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        // when
        sut.execute(out, err);
        // then
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-source.xml"));
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-target.xml"));
        assertThat(asList(outputFolderFile.list()), hasItem("invoice-cen.csv"));
        assertThat(asList(outputFolderFile.list()), hasItem("rule-report.csv"));
//        assertThat(asList(outputFolderFile.list()), hasItem("invoice-transformation.log"));
    }

    @Test
    public void fromCenConversionShouldCreateCsvIfConversionResultHasErrorsAndForceFlag() {
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInputStream)
                .setForceConversion(true)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .setApi(api)
                .setInvoiceInName(plainFattPa.getName())
                .build();
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        // when
        sut.execute(out, err);
        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());
        assertTrue(outputFolderFile.exists());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-source.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-cen.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-target.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "rule-report.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "fromcen-errors.csv"), notNullValue());
    }

    @Test
    public void toCenConversionShouldCreateCsvIfConversionResultHasErrors() {
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
//        given(fromCen.extension()).willReturn(".xml");
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInputStream)
                .setForceConversion(false)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .setApi(api)
                .setInvoiceInName(plainFattPa.getName())
                .build();
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
    public void toCenConversionShouldCreateCsvAndAllFilesIfConversionResultHasErrorsButForceIsTrue() {
        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        // given
//        given(fromCen.extension()).willReturn(".xml");
        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInputStream)
                .setForceConversion(true)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .setApi(api)
                .setInvoiceInName(plainFattPa.getName())
                .build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        // when
        sut.execute(out, err);
        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "tocen-errors.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-source.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-cen.csv"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-target.xml"), notNullValue());
        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "rule-report.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());
    }
}
