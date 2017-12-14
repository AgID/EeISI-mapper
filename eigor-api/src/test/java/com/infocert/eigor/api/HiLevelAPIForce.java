package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class HiLevelAPIForce {

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
    public void usingEnableForceShouldHaveResultForBadInvoice() throws IOException, ConfigurationException {
        EigorApi api = new EigorApiBuilder()
                .withOutputFolder(outputFolderFile)
                .enableForce() // enable force flag
                .build();

        InputStream invoiceStream = getClass().getResourceAsStream("/examples/ubl/UBL-Invoice-2.1-Example-KO.xml");

        ConversionResult<byte[]> outcome = api
                .convert(
                        "ubl",
                        "fatturapa",
                        invoiceStream);

        assertTrue(outcome.hasResult());


    }

}
