package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static it.infocert.eigor.test.Utils.invoiceAsStream;

public class IssuesTest {

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();

    private static EigorConfiguration configuration;

    @BeforeClass
    public static void setUpConf() throws IOException {
        File fattpaCenFolder = tmpFolder.newFolder();
        configuration = Utils.setUpProperties(fattpaCenFolder, tmpFolder.getRoot());
    }




    @Test
    public void issue252() throws SyntaxErrorInInvoiceFormatException, ConfigurationException {


        InputStream sourceInvoiceStream = invoiceAsStream("/issues/issue-252-fattpa.xml");

        FattPa2Cen f2c = new FattPa2Cen(new JavaReflections(), configuration);
        f2c.configure();

        ConversionResult<BG0000Invoice> result = f2c.convert(sourceInvoiceStream);

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0112InvoiceTotalAmountWithVat bt112 = docTotals.getBT0112InvoiceTotalAmountWithVat(0);

            Assert.assertEquals("122.00", bt112.getValue().toString());
        }

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0115AmountDueForPayment bt115 = docTotals.getBT0115AmountDueForPayment(0);
            Assert.assertEquals("122.00", bt115.getValue().toString());
        }

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0113PaidAmount bt113 = docTotals.getBT0113PaidAmount(0);
            Assert.assertEquals(BigDecimal.ZERO, bt113.getValue());

        }

        {
            BG0000Invoice cen = result.getResult();
            String bt020 = cen.getBT0020PaymentTerms().get(0).getValue();
            Assert.assertEquals("N/A Payment Terms", bt020);

        }

        {
            BG0000Invoice cen = result.getResult();
            String bt032 = cen.getBG0004Seller().get(0).getBT0032SellerTaxRegistrationIdentifier().get(0).getValue();
            Assert.assertEquals("02313821007", bt032);

        }


    }


}
