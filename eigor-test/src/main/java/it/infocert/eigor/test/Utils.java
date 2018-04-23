package it.infocert.eigor.test;

import org.junit.Assert;

import java.io.InputStream;

public class Utils {

    public static InputStream invoiceAsStream(String invoicePath) {
        InputStream inputFatturaPaXml = Utils.class.getResourceAsStream(invoicePath);
        Assert.assertNotNull("No documents found at path '" + invoicePath + "'", inputFatturaPaXml);
        return inputFatturaPaXml;
    }

}
