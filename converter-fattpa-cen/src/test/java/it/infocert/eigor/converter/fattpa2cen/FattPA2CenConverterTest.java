package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.io.Resources;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class FattPA2CenConverterTest {

    @Test
    public void test() throws Exception {
        URL italianInvoiceUrl = Resources.getResource("fatt-pa-plain-vanilla.xml");
        FattPA2CenConverter converter = new FattPA2CenConverter();
        converter.convert(italianInvoiceUrl.getFile());
    }
}