package it.infocert.eigor.converter.cii2cen;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.IXMLValidator;
import it.infocert.eigor.api.SchematronValidator;
import it.infocert.eigor.api.XSDValidator;

public class Cii2CenTest {

	private static final Logger log = LoggerFactory.getLogger(Cii2CenTest.class);

	private Cii2Cen sut;
	
	@Before
	public void setUp() {
		sut = new Cii2Cen(new Reflections("it.infocert"));
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
	public void testShouldValidateXsd() throws IOException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1.xml");
		List<ConversionIssue> errors = validateXSD(sourceInvoiceStream);
		assertTrue(errors.isEmpty());
	}
	
	@Test
	public void testShouldNotValidateXsd() throws IOException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		List<ConversionIssue> errors = validateXSD(sourceInvoiceStream);
		assertEquals("XSD validation error at 17:22. cvc-complex-type.2.4.a: contenuto non valido che inizia con l'elemento \"ram:IssueDateTime\". È previsto un elemento \"{\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100\":ID}\".", errors.get(0).getMessage());
	}
	
	@Test
	public void testShouldValidateSchematron() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1.xml");
		List<ConversionIssue> errors = validateSchematron(sourceInvoiceStream);
	   	assertTrue(errors.isEmpty());
	}
	
	@Test
	public void testShouldNotValidateSchematron() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		List<ConversionIssue> errors = validateSchematron(sourceInvoiceStream);
		assertEquals("Schematron failed assert '[BR-02]-An Invoice shall have an Invoice number.' on XML element at '/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]'.", errors.get(0).getMessage());
		assertEquals("Schematron failed assert '[BR-04]-An Invoice shall have an Invoice type code.' on XML element at '/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]'.", errors.get(1).getMessage());
		assertEquals("Schematron failed assert '[CII-SR-014] - TypeCode must exist exactly once' on XML element at '/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:ExchangedDocument[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]'.", errors.get(2).getMessage());
	}
	
	private List<ConversionIssue> validateXSD(InputStream sourceInvoiceStream) throws IOException {
	   	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
	   	String filePath = getClass().getClassLoader().getResource("xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd").getFile();
	   	File xsdFile = new File(filePath);
	   	XSDValidator xsdValidator = new XSDValidator(xsdFile);
	   	return xsdValidator.validate(bytes);
	}
	
	private List<ConversionIssue> validateSchematron(InputStream sourceInvoiceStream) throws IOException {
		byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
		String filePath = getClass().getClassLoader().getResource("schematron-xslt/EN16931-CII-validation.xslt").getFile();
		File schematronFile = new File(filePath);
		IXMLValidator ciiValidator = new SchematronValidator(schematronFile, true);
		return ciiValidator.validate(bytes);
	}

}
