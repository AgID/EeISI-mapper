package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    public static EigorConfiguration setUpProperties(File fattpaCenFolder, File eigorWorkDir) throws IOException {
        Properties properties = new Properties();

        //eigor.converter.fatturapa-cen.xsd=classpath:converterdata/converter-commons/fattpa/xsdstatic/Schema_del_file_xml_FatturaPA_versione_1.2.xsd
        {
            File xsdFile = new File(fattpaCenFolder, "Schema_del_file_xml_FatturaPA_versione_1.2.xsd");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-commons/fattpa/xsdstatic/Schema_del_file_xml_FatturaPA_versione_1.2.xsd"),
                    xsdFile);
            properties.put("eigor.converter.fatturapa-cen.xsd", "file:///" + xsdFile.getAbsolutePath());

            File xsdFile2 = new File(fattpaCenFolder, "imported");
            xsdFile2.mkdirs();
            xsdFile2 = new File(xsdFile2, "xmldsig-core-schema.xsd");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-commons/fattpa/xsdstatic/imported/xmldsig-core-schema.xsd"),
                    xsdFile2);

        }

        //eigor.converter.fatturapa-cen.mapping.one-to-one=classpath:converterdata/converter-fattpa-cen/mappings/one_to_one.properties
        {
            File file = new File(fattpaCenFolder, "one_to_one.properties");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-fattpa-cen/mappings/one_to_one.properties"),
                    file);
            properties.put("eigor.converter.fatturapa-cen.mapping.one-to-one", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.custom=classpath:converterdata/converter-fattpa-cen/mappings/custom.conf
        {
            File file = new File(fattpaCenFolder, "custom.conf");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-fattpa-cen/mappings/custom.conf"),
                    file);
            properties.put("eigor.converter.fatturapa-cen.mapping.custom", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.one-to-many=classpath:converterdata/converter-fattpa-cen/mappings/one_to_many.properties
        {
            File file = new File(fattpaCenFolder, "one_to_many.properties");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-fattpa-cen/mappings/one_to_many.properties"),
                    file);
            properties.put("eigor.converter.fatturapa-cen.mapping.one-to-many", "file:///" + file.getAbsolutePath());
        }

        //eigor.converter.fatturapa-cen.mapping.many-to-one=classpath:converterdata/converter-fattpa-cen/mappings/many_to_one.properties
        {
            File file = new File(fattpaCenFolder, "many_to_one.properties");
            FileUtils.copyInputStreamToFile(
                    IssuesTest.class.getResourceAsStream("/converterdata/converter-fattpa-cen/mappings/many_to_one.properties"),
                    file);
            properties.put("eigor.converter.fatturapa-cen.mapping.many-to-one", "file:///" + file.getAbsolutePath());
        }

        properties.put("eigor.workdir", eigorWorkDir.getAbsolutePath());

        EigorConfiguration propertiesBackedConfiguration = new PropertiesBackedConfiguration(properties);

        return propertiesBackedConfiguration;
    }

}
