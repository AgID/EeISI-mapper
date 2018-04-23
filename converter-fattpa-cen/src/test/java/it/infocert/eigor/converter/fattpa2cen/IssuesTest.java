package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.mockito.Mockito.mock;

public class IssuesTest {

    @Test
    public void issue252() throws SyntaxErrorInInvoiceFormatException, ConfigurationException {

        EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();
        InputStream sourceInvoiceStream = invoiceAsStream("/issues/issue-252-fattpa.xml");

        FattPa2Cen f2c = new FattPa2Cen(new JavaReflections(), configuration);
        f2c.configure();

        ConversionResult<BG0000Invoice> result = f2c.convert(sourceInvoiceStream);

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0112InvoiceTotalAmountWithVat bt12 = docTotals.getBT0112InvoiceTotalAmountWithVat(0);

            Assert.assertEquals(122.0, bt12.getValue(), 0.0001);
        }

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0115AmountDueForPayment bt115 = docTotals.getBT0115AmountDueForPayment(0);
            Assert.assertEquals(122.0, bt115.getValue(), 0.0001);
        }

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0113PaidAmount bt113 = docTotals.getBT0113PaidAmount(0);
            Assert.assertEquals(0.0, bt113.getValue(), 0.0001);

        }

        {
            BG0000Invoice cen = result.getResult();
            String bt020 = cen.getBT0020PaymentTerms().get(0).getValue();
            Assert.assertEquals("N/A Payement Terms", bt020);

        }



    }


}
