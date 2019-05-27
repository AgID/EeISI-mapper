package com.infocert.eigor.api;

import org.junit.Test;
import utils.XxeChecker;

import java.io.InputStream;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XxeCheckerTest {


    @Test
    public void PassXxeChecker() {
        InputStream inputFatturaPaXmlOk = invoiceAsStream("/issues/fattpa_without_xxe.xml");
        assertTrue(XxeChecker.parser(inputFatturaPaXmlOk));

        InputStream inputFatturaPaXmlKo = invoiceAsStream("/issues/fattpa_with_xxe.xml");
        assertFalse(XxeChecker.parser(inputFatturaPaXmlKo));
    }
}
