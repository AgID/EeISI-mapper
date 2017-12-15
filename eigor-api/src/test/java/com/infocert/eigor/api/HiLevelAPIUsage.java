package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
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
    public void hiLevelConversion() throws IOException, ConfigurationException {

        // 1. Construct an instance of EigorAPI using the related builder.
        // The API obtained is thread safe and can be then used to convert multiple invoices.
        // So, there's no need to instantiate EigorApi api each time even because its initialization takes time.
        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        // 2. Load the invoice to be converted as a stream
        InputStream invoiceAsStream = new ByteArrayInputStream("<invoice>data</invoice>".getBytes());

        // 3. Execute the conversion specifying the source formatPadded, the target formatPadded and the invoice to be transformed.
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

}
