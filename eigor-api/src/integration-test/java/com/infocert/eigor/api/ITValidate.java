package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static org.junit.Assert.*;

public class ITValidate {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File invoice;
    private File brokenInvoice;
    private EigorApi api;

    @Before
    public void setUp() throws Exception {
        final ClassLoader classLoader = this.getClass().getClassLoader();

        final URL resource = classLoader.getResource("working-examples/ubl-invoice.to-fattpa-working-example.xml");
        final URL brokenResource = classLoader.getResource("broken-examples/ubl-invoice.to-fattpa-broken-example.xml");

        assert resource != null;
        assert brokenResource != null;

        invoice = new File(resource.getFile());
        brokenInvoice = new File(brokenResource.getFile());

        final File outputFolderFile = tmp.newFolder();
        if (!outputFolderFile.exists()) outputFolderFile.mkdirs();

        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(outputFolderFile)
                .enableForce()
                .build();
    }

    @Test
    public void shouldValidateAValidInvoice() throws Exception {
        try (final FileInputStream fis = new FileInputStream(invoice)) {
            final ConversionResult<Void> result = api.validate("ubl", fis);
            assertNotNull(result);
            assertTrue(result.isSuccessful());
            assertFalse(result.hasIssues());
        }
    }

    @Test
    public void shouldNotValidateAnInvalidInvoice() throws Exception {
        try (final FileInputStream fis = new FileInputStream(brokenInvoice)) {
            final ConversionResult<Void> result = api.validate("ubl", fis);
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertTrue(result.hasIssues());
        }
    }

    @Test
    public void shouldFindOneSemanticIssue() throws Exception {
        try (final FileInputStream fis = new FileInputStream(brokenInvoice)) {
            final ConversionResult<Void> result = api.validateSyntax("ubl", fis);
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertTrue(result.hasIssues());
            assertEquals(result.getIssues().size(), 1);
        }
    }
}
