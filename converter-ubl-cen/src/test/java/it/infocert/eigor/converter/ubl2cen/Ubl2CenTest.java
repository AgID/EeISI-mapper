package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.XSDValidator;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class Ubl2CenTest {

    private static final Logger log = LoggerFactory.getLogger(ITUbl2CenTest.class);

    private Ubl2Cen sut;

    @Before
    public void setUp() {
        sut = new Ubl2Cen(
                new Reflections("it.infocert"),
                mock(EigorConfiguration.class)
        );
    }

    @Test
    public void shouldSupportUbl() {
        assertThat(sut.support("ubl"), is(true));
    }

    @Test
    public void shouldSupportedFormatsUbl() {
        assertThat(sut.getSupportedFormats(), contains("ubl"));
    }
    
    @Test
    public void shouldValidateXsd() throws Exception {
    	InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example.xml");
    	List<IConversionIssue> errors = validate(sourceInvoiceStream);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotValidateXsd() throws Exception {
    	InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example-KO.xml");
    	List<IConversionIssue> errors = validate(sourceInvoiceStream);
    	assertFalse(errors.isEmpty());
    }

    
    private List<IConversionIssue> validate(InputStream sourceInvoiceStream) throws IOException, SAXException {
	   	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
	   	String filePath = getClass().getClassLoader().getResource("xsd/UBL-Invoice-2.1.xsd").getFile();
	   	File xsdFile = new File(filePath);
	   	XSDValidator xsdValidator = new XSDValidator(xsdFile);
	   	return xsdValidator.validate(bytes);
   }
}