package it.infocert.eigor.api;



import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.impl.*;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class ApiTest {

    @Test public void exampleOfAConversion() throws SyntaxErrorInInvoiceFormatException {

        // services
        IReflections reflections = new JavaReflections();
        PropertiesBackedConfiguration configuration = new PropertiesBackedConfiguration();

        ToCenConversionRepository conversionRepository = new ToCenListBakedRepository(
                new FakeToCenConversion(reflections, configuration)
        );
        FromCenConversionRepository fromCenConversionRepository = new FromCenListBakedRepository(
                new FakeFromCenConversion(reflections, configuration)
        );

        // arguments
        String soureFormat = "fake";
        String targetFormat = "fake";
        InputStream invoiceInSourceFormat = new ByteArrayInputStream( "<invoice>this is an invoice</invoice>".getBytes() );

        // preconditions
        ToCenConversion toCen = conversionRepository.findConversionToCen(soureFormat);
        FromCenConversion fromCen = fromCenConversionRepository.findConversionFromCen(targetFormat);

        // business logic
        final BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat).getResult();
        byte[] converted = fromCen.convert(cenInvoice).getResult();

    }

}
