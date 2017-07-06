package it.infocert.eigor.converter.cii2cen;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.XSDValidator;
import org.xml.sax.SAXException;

public class Cii2CenTest {

	private static final Logger log = LoggerFactory.getLogger(Cii2CenTest.class);

	private Cii2Cen sut;
	
	@Before
	public void setUp() {
		EigorConfiguration conf = new PropertiesBackedConfiguration();
		sut = new Cii2Cen(new Reflections("it.infocert"), conf);
	}
		
	@Test
	public void shouldSupportCii() {
		assertThat(sut.support("cii"), is(true));
	}
	
	@Test
	public void shouldNotSupportCii() {
		assertThat(sut.support("fake"), is(false));
	}

	@Test
	public void shouldSupportedFormatsCii() {
		assertThat(sut.getSupportedFormats(), contains("cii"));
	}
	
	@Test
	public void testNullFormat() {
		assertFalse(sut.support(null));
	}

	@Test
	public void testShouldValidateXsd() throws IOException, SAXException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1.xml");
		List<ConversionIssue> errors = validate(sourceInvoiceStream);
		assertTrue(errors.isEmpty());
	}

	@Test
	public void testShouldNotValidateXsd() throws IOException, SAXException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		List<ConversionIssue> errors = validate(sourceInvoiceStream);
		assertFalse(errors.isEmpty());
	}

	private List<ConversionIssue> validate(InputStream sourceInvoiceStream) throws IOException, SAXException {
	   	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
	   	String filePath = getClass().getClassLoader().getResource("xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd").getFile();
	   	File xsdFile = new File(filePath);
	   	XSDValidator xsdValidator = new XSDValidator(xsdFile);
	   	return xsdValidator.validate(bytes);
   }

}
