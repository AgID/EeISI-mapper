package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import static it.infocert.eigor.test.Files.findFirstFileByNameOrNull;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConversionCommandTest {

    private String sourceFormat = "ubl";
    private String targetFormat = "xmlcen";

    static EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();

    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();

    private File outputFolderFile;

    @Before
    public void setUpOutputFolder() throws IOException {
        outputFolderFile = tmpRule.newFolder("output");
    }

    @Ignore
    @Test
    public void shouldUseTheExtensionSpecifiedFromTheFromCenConverterAsOutputExtension() {

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceInSourceFormat = new ByteArrayInputStream("<invoice>invoice</invoice>".getBytes());

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInSourceFormat)
                .setForceConversion(false)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .build();

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

    @Ignore
    @Test
    public void fromCenConversionShouldCreateCsvIfConversionResultHasErrorsAndForceFlag() {

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceInSourceFormat = new ByteArrayInputStream("<invoice>invoice</invoice>".getBytes());

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInSourceFormat)
                .setForceConversion(true)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .build();
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

    @Ignore
    @Test
    public void toCenConversionShouldCreateCsvIfConversionResultHasErrors() throws IOException, SyntaxErrorInInvoiceFormatException {

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceInSourceFormat = new ByteArrayInputStream("<invoice>invoice</invoice>".getBytes());
//        given(fromCen.extension()).willReturn(".xml");

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInSourceFormat)
                .setForceConversion(false)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false).build();
        PrintStream err = new PrintStream(new ByteArrayOutputStream());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());

        // when
        sut.execute(out, err);

        // then a fromcen-errors.csv should be created for the issues along with the other files
        List<File> files = asList(outputFolderFile.listFiles());

        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "tocen-errors.csv"), notNullValue());
//        assertThat(files + " found", findFirstFileByNameOrNull(outputFolderFile, "invoice-transformation.log"), notNullValue());
    }

    @Ignore
    @Test
    public void toCenConversionShouldCreateCsvAndAllFilesIfConversionResultHasErrorsButForceIsTrue() throws IOException, SyntaxErrorInInvoiceFormatException {

        Path outputFolder = FileSystems.getDefault().getPath(outputFolderFile.getAbsolutePath());
        InputStream invoiceInSourceFormat = new ByteArrayInputStream("<invoice>invoice</invoice>".getBytes());
        // given
//        given(fromCen.extension()).willReturn(".xml");

        ConversionCommand sut = new ConversionCommand.ConversionCommandBuilder()
                .setTargetFormat(targetFormat)
                .setSourceFormat(sourceFormat)
                .setOutputFolder(outputFolder)
                .setInvoiceInSourceFormat(invoiceInSourceFormat)
                .setForceConversion(true)
                .setConfiguration(configuration)
                .setRunIntermediateValidation(false)
                .build();
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
