package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.api.utils.ResultUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0016PaymentInstructions;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0035SellerAddressLine1;
import it.infocert.eigor.model.core.model.BT0082PaymentMeansText;
import it.infocert.eigor.model.core.model.BT0106SumOfInvoiceLineNetAmount;
import it.infocert.eigor.model.core.model.BT0124ExternalDocumentLocation;
import it.infocert.eigor.model.core.model.BT0152InvoicedItemVatRate;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.junit.Assert.assertFalse;

public class XmlCen2CenTest {

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();
    private static Properties properties;

    @Before
    public void setup() throws IOException {
        File xmlcen2CenFolder = tmpFolder.newFolder();

        properties = new Properties();

        {
            File xsdFile = new File(xmlcen2CenFolder, "schema.xsd");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-commons/xmlcen/xsdstatic/semanticCEN0.0.2.xsd"),
                    xsdFile);
            properties.put("eigor.converter.xmlcen-cen.xsd", "file:///" + xsdFile.getAbsolutePath());
        }

        {
            File file = new File(xmlcen2CenFolder, "schematron.sch");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-commons/xmlcen/schematron-xslt/EN16931-CEN-Model-and-CodeList.xslt"),
                    file);
            properties.put("eigor.converter.xmlcen-cen.schematron", "file:///" + file.getAbsolutePath());
            properties.put("eigor.converter.xmlcen-cen.schematron.auto-update-xslt", "false");
        }

        {
            File file = new File(xmlcen2CenFolder, "one_to_one.properties");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-xmlcen-cen/mappings/one_to_one.properties"),
                    file);
            properties.put("eigor.converter.xmlcen-cen.mapping.one-to-one", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.custom=classpath:converterdata/converter-xmlcen-cen/mappings/custom.conf
        {
            File file = new File(xmlcen2CenFolder, "custom.conf");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-xmlcen-cen/mappings/custom.conf"),
                    file);
            properties.put("eigor.converter.xmlcen-cen.mapping.custom", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.one-to-many=classpath:converterdata/converter-xmlcen-cen/mappings/one_to_many.properties
        {
            File file = new File(xmlcen2CenFolder, "one_to_many.properties");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-xmlcen-cen/mappings/one_to_many.properties"),
                    file);
            properties.put("eigor.converter.xmlcen-cen.mapping.one-to-many", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.many-to-one=classpath:converterdata/converter-xmlcen-cen/mappings/many_to_one.properties
        {
            File file = new File(xmlcen2CenFolder, "many_to_one.properties");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-xmlcen-cen/mappings/many_to_one.properties"),
                    file);
            properties.put("eigor.converter.xmlcen-cen.mapping.many-to-one", "file:///" + file.getAbsolutePath());
        }

    }

    @Test
    public void convertXmlMandatoryFieldsOnly() throws ConfigurationException, SyntaxErrorInInvoiceFormatException {
        EigorConfiguration configuration = new PropertiesBackedConfiguration(properties);

        InputStream sourceInvoiceStream = invoiceAsStream("/examples/xmlcen/semanticCEN.xml");

        XmlCen2Cen xmlCen2Cen = new XmlCen2Cen(new JavaReflections(), configuration);
        xmlCen2Cen.configure();

        ConversionResult<BG0000Invoice> result = xmlCen2Cen.convert(sourceInvoiceStream);

        assertFalse(ResultUtils.toString(result), result.hasIssues());

        {
            BG0000Invoice cen = result.getResult();
            final BT0035SellerAddressLine1 bt35 = cen.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0035SellerAddressLine1().get(0);

            Assert.assertEquals("Via Carlo Bo", bt35.getValue());
        }

        {
            BG0000Invoice cen = result.getResult();
            BG0022DocumentTotals docTotals = cen.getBG0022DocumentTotals(0);
            BT0106SumOfInvoiceLineNetAmount bt106 = docTotals.getBT0106SumOfInvoiceLineNetAmount().get(0);

            Assert.assertEquals("1100", bt106.getValue().toString());
        }

        {
            BG0000Invoice cen = result.getResult();
            final BT0152InvoicedItemVatRate bt152 = cen.getBG0025InvoiceLine().get(0).getBG0030LineVatInformation().get(0).getBT0152InvoicedItemVatRate().get(0);

            Assert.assertEquals("10.00", bt152.getValue().toString());
        }
    }

    @Test
    public void convertXmlAllFields() throws ConfigurationException, SyntaxErrorInInvoiceFormatException {
        EigorConfiguration configuration = new PropertiesBackedConfiguration(properties);

        InputStream sourceInvoiceStream = invoiceAsStream("/examples/xmlcen/Test_EeISI_300_CENfullmodel.xml");

        XmlCen2Cen xmlCen2Cen = new XmlCen2Cen(new JavaReflections(), configuration);
        xmlCen2Cen.configure();

        ConversionResult<BG0000Invoice> result = xmlCen2Cen.convert(sourceInvoiceStream);

        assertFalse(result.hasIssues());

        {
            BG0000Invoice cen = result.getResult();
            final BT0035SellerAddressLine1 bt35 = cen.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0035SellerAddressLine1().get(0);
            Assert.assertEquals("Seller address line 1", bt35.getValue());
        }

        {
            BG0000Invoice cen = result.getResult();
            final BG0016PaymentInstructions bg16 = cen.getBG0016PaymentInstructions().get(0);
            final BT0082PaymentMeansText bt82 = bg16.getBT0082PaymentMeansText().get(0);

            Assert.assertEquals("SEPA", bt82.getValue());
        }

        {
            BG0000Invoice cen = result.getResult();
            final BT0124ExternalDocumentLocation bt124 = cen.getBG0024AdditionalSupportingDocuments().get(0).getBT0124ExternalDocumentLocation().get(0);

            Assert.assertEquals("External document location", bt124.getValue());
        }
    }
}
