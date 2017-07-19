package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.PropertiesWithReplacement;
import it.infocert.eigor.api.io.Copier;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class XSDValidatorTest {

    @Test
    public void shouldExportXSDToPathAndLoadFromThere() throws IOException, SAXException {

        // ...a conig pointing to a workdir
        PropertiesWithReplacement config = new PropertiesWithReplacement();
        config.setProperty("workdir", "${prop.java.io.tmpdir}" + File.separator + "eigor");

        // ...a copy of the jar content in the workdir
        File workdir = new File(config.getProperty("workdir"));
        Copier copier = new Copier(workdir);
        copier.copyFromJar("/test-converterdata/test-converter-cii-cen/cii");

        // ...then the instantiation should complete without failure
        File xsd = new File(workdir.getAbsolutePath() + "/xsd/uncoupled/data/standard/CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd".replace("/", File.separator));
        new XSDValidator( xsd );

    }

    @Test
    public void shouldLoadXSDFromJar() throws IOException, SAXException {
        DefaultResourceLoader drl = new DefaultResourceLoader();
        Resource resource = drl.getResource("classpath:test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd");
        URI uri = resource.getURI();

        // try to use it as a stream.
        InputStream inputStream = resource.getInputStream();
        assertThat(inputStream.read(), not(is(-1)));
        inputStream.close();

        // try to read it as a file. It is not possible because this file is in a Jar
        try{
            resource.getFile();
            fail();
        }catch(FileNotFoundException fnfe){
            // ok expected!
        }

        // let's try loading it into a validator
        try {
            new XSDValidator(resource.getInputStream());
            fail();
        }catch (SAXParseException spe){
            // a spe is thrown because this XSD has references to other XSDs, so you cannot load it from a stream.
        }

    }

}