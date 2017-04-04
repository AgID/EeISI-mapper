package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;

import java.io.FileInputStream;

public class FattPA2CenConverterTest {

//    @Test
    public void test() throws Exception {
        FattPA2CenConverter converter = new FattPA2CenConverter();
        final FileInputStream xmlFile = new FileInputStream("target/test-classes/example-fattPa.xml");
        BG0000Invoice invoice = converter.convert(xmlFile);
        System.out.print(invoice.toString());
    }
}