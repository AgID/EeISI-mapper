package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static it.infocert.eigor.test.Utils.invoiceAsStream;

public class XmlCen2CenTest {

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();
    private static Properties properties;

    @Before
    public void setup() throws IOException {
        File xmlcen2CenFolder = tmpFolder.newFolder();

        properties = new Properties();

        {
            File xsdFile = new File(xmlcen2CenFolder, "semanticCEN0.0.2.xsd");
            FileUtils.copyInputStreamToFile(
                    XmlCen2CenTest.class.getResourceAsStream("/converterdata/converter-commons/xmlcen/xsdstatic/semanticCEN0.0.2.xsd"),
                    xsdFile);
            properties.put("eigor.converter.xmlcen-cen.xsd", "file:///" + xsdFile.getAbsolutePath());
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
    public void convert() throws ConfigurationException, SyntaxErrorInInvoiceFormatException {
        EigorConfiguration configuration = new PropertiesBackedConfiguration(properties);

        InputStream sourceInvoiceStream = invoiceAsStream("/semanticCEN.xml");

        XmlCen2Cen xmlCen2Cen = new XmlCen2Cen(new JavaReflections(), configuration);
        xmlCen2Cen.configure();

        ConversionResult<BG0000Invoice> result = xmlCen2Cen.convert(sourceInvoiceStream);
    }
}