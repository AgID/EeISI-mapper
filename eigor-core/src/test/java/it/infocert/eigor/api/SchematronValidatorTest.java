package it.infocert.eigor.api;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class SchematronValidatorTest {

    SchematronValidator schematronValidator;
    byte[] sampleXml;

    @Before
    public void setUp() throws Exception {
        schematronValidator = new SchematronValidator(new File("src/test/resources/validator/dogs/dogs.sch"), false);
        sampleXml = Files.readAllBytes(Paths.get("src/test/resources/validator/dogs/dogs.xml"));
    }

    @Test
    public void shouldNotFailParsingSchematron() {
        List<IConversionIssue> issueList = schematronValidator.validate(sampleXml);
        for (IConversionIssue issue : issueList) {
            assertFalse(issue.getErrorMessage().getMessage().contains("Schematron parsing failed."));
        }
    }
}