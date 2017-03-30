package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;

import java.io.File;

public class FattPA2CenConverterTest {

    @Test
    public void test() throws Exception {
        FattPA2CenConverter converter = new FattPA2CenConverter();
        final File xmlFile = new File("target/test-classes/example-fattPa.xml");
//        C:\Users\Matteo\Software\eigor\converter-fattpa-cen\converter-fattpa-cen\target\test-classes\example-fattPa.xml
//        C:\Users\Matteo\Software\eigor\converter-fattpa-cen\target\test-classes\example-fattPa.xml
        BG0000Invoice invoice = converter.convert(xmlFile);
        System.out.print(invoice.toString());
    }
}