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

        ByteArrayInputStream invoiceAsStream = new ByteArrayInputStream("<invoice>xml</invoice>".getBytes());



        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .build();

        ConversionResult<byte[]> outcome = api
                .convert(
                        "ubl",
                        "fatturapa",
                        invoiceAsStream);



        boolean hasErrors = outcome.hasIssues(); // to check if the convertion finisched with issues.
        List<IConversionIssue> issues = outcome.getIssues(); // to get the list of issues.
        outcome.hasResult(); // to check if it has a result.
        byte[] result = outcome.getResult(); // to get the resulting XML (if any).

    }

}
