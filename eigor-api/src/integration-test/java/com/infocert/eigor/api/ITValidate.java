package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.errors.ErrorCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Objects;

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
    public void shouldFindOneSyntaxIssue() throws Exception {
        try (final FileInputStream fis = new FileInputStream(brokenInvoice)) {
            final ConversionResult<Void> result = api.validateSyntax("ubl", fis);
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertTrue(result.hasIssues());
            assertEquals(1, result.getIssues().size());
        }
    }

    @Test
    public void shouldFindOneSyntaxAndTwoSemanticIssue() throws Exception {
        try (final FileInputStream fis = new FileInputStream(brokenInvoice)) {
            final ConversionResult<Void> result = api.validateSemantic("ubl", fis);
            assertNotNull(result);
            assertFalse(result.isSuccessful());
            assertTrue(result.hasIssues());

            final long syntaxIssues = result.getIssues().stream()
                    .filter(i -> ErrorCode.Action.XSD_VALIDATION.equals(Objects.requireNonNull(i.getErrorMessage().getErrorCode()).getAction()))
                    .count();
            assertEquals(1L, syntaxIssues);

            final long semanticIssues = result.getIssues().stream()
                    .filter(i -> ErrorCode.Action.SCH_VALIDATION.equals(Objects.requireNonNull(i.getErrorMessage().getErrorCode()).getAction()))
                    .count();
            assertEquals(2L, semanticIssues);
        }
    }

}
