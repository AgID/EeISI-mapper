package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.EigorException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class HiLevelAPIUsage {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    Logger log = LoggerFactory.getLogger(this.getClass());

    File outputFolderFile;

    @Before
    public void setUp() throws IOException {
        outputFolderFile = tmp.newFolder();
        if (!outputFolderFile.exists()) outputFolderFile.mkdirs();
    }

    @Test
    public void shouldGetSupportInfo() throws Exception {

        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        assertThat( api.getDetailedVersion(), not( isEmptyOrNullString() ) );
        assertThat( api.getVersion(), not( isEmptyOrNullString() ) );
        assertThat( api.supportedSourceFormats(), not(nullValue()) );
        assertThat( api.supportedTargetFormats(), anyOf(notNullValue(), not(empty())) );

    }

    @Test
    public void schSchematronConversion() throws IOException, EigorException {

        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        ConversionResult<Void> failedValidation = api.customSchSchematronValidation(
                new File(getClass().getResource("/dogs/dogs.sch").getFile()),
                getClass().getResourceAsStream("/dogs/dogs.xml")
        );

        assertThat( failedValidation.getIssues(), Matchers.hasSize(4) );

    }

    @Test
    public void xsdConversion() throws IOException, EigorException {

        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        ConversionResult<Void> failedValidation = api.customXsdValidation(
                new File(getClass().getResource("/dogs/dogs.xsd").getFile()),
                getClass().getResourceAsStream("/dogs/dogs.xml")
        );

        assertThat( failedValidation.getIssues().toString(), failedValidation.getIssues(), Matchers.hasSize(4) );

    }

    @Test
    public void hiLevelConversion() throws IOException, ConfigurationException {

        // 1. Construct an instance of EigorAPI using the related builder.
        // The API obtained is thread safe and can be then used to convert multiple invoices.
        // So, there's no need to instantiate EigorApi api each time even because its initialization takes time.
        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        // 2. Load the invoice to be converted as a stream
        InputStream invoiceAsStream = new ByteArrayInputStream("<invoice>data</invoice>".getBytes());

        // 3. Execute the conversion specifying the source format, the target format and the invoice to be transformed.
        ConversionResult<byte[]> outcome = api
                .convert(
                        "ubl",
                        "fatturapa",
                        invoiceAsStream);

        // 4. You have now multiple ways to query the outcome object.

        // ...check if the convertion finished with issues.
        boolean hasErrors = outcome.hasIssues();

        // ...get the complete list of occurred issues.
        List<IConversionIssue> issues = outcome.getIssues();

        // ...whether a converted invoice is available.
        outcome.hasResult();

        // ...and in this case you can obtain the produced XML.
        byte[] result = outcome.getResult();

    }


    @Test
    public void hiLevelValidation() throws IOException, ConfigurationException {

        // 1. Construct an instance of EigorAPI using the related builder.
        // The API obtained is thread safe and can be then used to validate multiple invoices.
        // So, there's no need to instantiate EigorApi api each time even because its initialization takes time.
        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        // 2. Load the invoice to be validated as a stream
        InputStream invoiceAsStream = new ByteArrayInputStream("<invoice>data</invoice>".getBytes());

        // 3. Execute the validation specifying the source format and the invoice to be transformed.
        ConversionResult<Void> outcome = api
                .validate(
                        "ubl",
                        invoiceAsStream);

        // 4. The returned object is the same as for the conversion, only that it doesn't hold any result.

        boolean successful = outcome.isSuccessful();

        // This will always return false.
        outcome.hasResult();

    }
}
