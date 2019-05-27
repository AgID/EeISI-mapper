package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.EigorException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.Test;
import utils.XxeChecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.junit.Assert.*;

public class XxeCheckerTest {

    @Test
    public void XxeCheckerTest() {
        InputStream inputFatturaPaXmlOk = invoiceAsStream("/issues/fattpa_without_xxe.xml");
        assertTrue(XxeChecker.parser(inputFatturaPaXmlOk));

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        assertFalse(XxeChecker.parser(inputFatturaPaXmlKo));
    }

    @Test
    public void XxeCheckerApiConversionTest() throws IOException, ConfigurationException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<byte[]> convert = eigorApi.convert("fatturapa", "xmlcen", inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }

    @Test
    public void XxeCheckerApiValidationTest() throws IOException, ConfigurationException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<Void> convert = eigorApi.validate("fatturapa", inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }

    @Test
    public void XxeCheckerApiSyntaxValidationTest() throws IOException, ConfigurationException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<Void> convert = eigorApi.validateSyntax("fatturapa", inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }

    @Test
    public void XxeCheckerApiSemanticValidationTest() throws IOException, ConfigurationException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<Void> convert = eigorApi.validateSemantic("fatturapa", inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }

    @Test
    public void XxeCheckerApiCustomSchematronValidationTest() throws IOException, EigorException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<Void> convert = eigorApi.customSchSchematronValidation(new File("fake_file.fake"), inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }

    @Test
    public void XxeCheckerApiCustomXsdValidationTest() throws IOException, EigorException {
        EigorApiBuilder eigorApiBuilder = new EigorApiBuilder();
        EigorApi eigorApi = eigorApiBuilder.build();

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        ConversionResult<Void> convert = eigorApi.customXsdValidation(new File("fake_file.fake"), inputFatturaPaXmlKo);
        assertTrue(convert.getIssues().size() > 0);
        assertNotNull(convert.getIssues().get(0).getErrorMessage());
        assertNotNull(convert.getIssues().get(0).getErrorMessage().getMessage());
        assertTrue(convert.getIssues().get(0).getErrorMessage().getMessage().contains("Input invoice XXE"));
    }
}
