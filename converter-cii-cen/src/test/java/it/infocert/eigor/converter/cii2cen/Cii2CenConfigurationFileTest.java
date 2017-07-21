package it.infocert.eigor.converter.cii2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Cii2CenConfigurationFileTest { //} extends Cii2Cen {

	private static final Logger log = LoggerFactory.getLogger(Cii2CenConfigurationFileTest.class);

	private MyCiiToCenConverter sut;
	
	@Before
	public void setUp() throws ConfigurationException {
		EigorConfiguration conf = new PropertiesBackedConfiguration()
				.addProperty("eigor.converter.cii-cen.mapping.one-to-one", "converterdata/converter-cii-cen/mappings/one_to_one.properties")
				.addProperty("eigor.converter.cii-cen.mapping.many-to-one", "converterdata/converter-cii-cen/mappings/many_to_one.properties")
				.addProperty("eigor.converter.cii-cen.xsd", "file:src/test/resources/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd")
				.addProperty("eigor.converter.cii-cen.schematron", "converterdata/converter-cii-cen/cii/schematron-xslt/EN16931-CII-validation.xslt");
		sut = new MyCiiToCenConverter(new Reflections("it.infocert"), conf);
		sut.configure();
	}

	@Test
	public void shouldAcceptACiiInvoiceMatchingTheCiiXsd() throws IOException, SAXException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1M.xml");
		List<ConversionIssue> errors = validateXmlWithCiiXsd(sourceInvoiceStream);
		assertTrue(errors.isEmpty());
	}

	@Test
	public void shouldRefuseACiiInvoiceNotValidAccordingToCiiXsd() throws IOException, SAXException {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		List<ConversionIssue> errors = validateXmlWithCiiXsd(sourceInvoiceStream);
		assertTrue(errors.size() == 1);
		ConversionIssue issue = errors.get(0);
		assertTrue(issue.getCause() instanceof SAXParseException);
		assertTrue(issue.isError());
		assertTrue(issue.getMessage().startsWith(IConstants.ERROR_XML_VALIDATION_ERROR));
	}

	@Test
	public void shouldAcceptACiiInvoiceThatSatisfiesTheCiiSchematron() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1M.xml");
		List<ConversionIssue> errors = validateXmlWithCiiSchematron(sourceInvoiceStream);
	   	assertTrue(errors.isEmpty());
	}

	@Test
	public void shouldRefuseACiiInvoiceThatDoesNotSatisfyTheCiiSchematron() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		List<ConversionIssue> errors = validateXmlWithCiiSchematron(sourceInvoiceStream);
		String temp;
		for(ConversionIssue conversionIssue : errors){
			temp = conversionIssue.getMessage();
			assertTrue(temp.contains("[BR-02]") || temp.contains("[BR-04]") || temp.contains("[CII-SR-014]"));
		}
	}

	@Test
	public void testOneToOneTrasformationMapping() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1M.xml");
		ConversionResult<BG0000Invoice> result = oneToOneMapping(sourceInvoiceStream);
		BG0000Invoice invoice = result.getResult();
		BT0005InvoiceCurrencyCode expected = new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
		assertEquals(expected, invoice.getBT0005InvoiceCurrencyCode(0));
	}

	@Test
	public void testFailOneToOneTrasformationMapping() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		ConversionResult<BG0000Invoice> result = oneToOneMapping(sourceInvoiceStream);
		BG0000Invoice invoice = result.getResult();
		assertTrue(invoice.getBT0001InvoiceNumber().isEmpty());
	}

	@Test
	public void testManyToOneTrasformationMapping() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example5M-ita-compliant.xml");
		ConversionResult<BG0000Invoice> result = manyToOneMapping(sourceInvoiceStream);
		BG0000Invoice invoice = result.getResult();
		BT0011ProjectReference expectedBT0011 = new BT0011ProjectReference("Project345 Project reference");
		BT0060PayeeIdentifierAndSchemeIdentifier expectedBT0060 = new BT0060PayeeIdentifierAndSchemeIdentifier("DK16356608 123456");
		assertEquals(expectedBT0011, invoice.getBT0011ProjectReference(0));
		assertEquals(expectedBT0060, invoice.getBG0010Payee().get(0).getBT0060PayeeIdentifierAndSchemeIdentifier(0));
	}

	@Test
	public void testFailManyToOneTrasformationMapping() throws Exception {
		InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/cii/CII_example1_KO.xml");
		ConversionResult<BG0000Invoice> result = manyToOneMapping(sourceInvoiceStream);
		BG0000Invoice invoice = result.getResult();
		assertTrue(invoice.getBT0011ProjectReference().isEmpty());
	}

	private List<ConversionIssue> validateXmlWithCiiXsd(InputStream sourceInvoiceStream) throws IOException, SAXException {
	   	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
	   	String filePath = getClass().getClassLoader().getResource("xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd").getFile();
	   	File xsdFile = new File(filePath);
	   	XSDValidator xsdValidator = new XSDValidator(xsdFile);
	   	return xsdValidator.validate(bytes);
	}
	
	private List<ConversionIssue> validateXmlWithCiiSchematron(InputStream sourceInvoiceStream) throws IOException {
		byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
		String filePath = getClass().getClassLoader().getResource("schematron-xslt/EN16931-CII-validation.xslt").getFile();
		File schematronFile = new File(filePath);
		IXMLValidator ciiValidator = new SchematronValidator(schematronFile, true);
		return ciiValidator.validate(bytes);
	}
	
	private ConversionResult<BG0000Invoice> oneToOneMapping(InputStream sourceInvoiceStream) throws Exception {
		byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
		InputStream clonedInputStream = new ByteArrayInputStream(bytes);
		Document document = getDocument(clonedInputStream);
		List<ConversionIssue> errors = new ArrayList<>();
		return sut.applyOne2OneTransformationsBasedOnMapping(document, errors);
	}

	private ConversionResult<BG0000Invoice> manyToOneMapping(InputStream sourceInvoiceStream) throws Exception {
		byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
		InputStream clonedInputStream = new ByteArrayInputStream(bytes);
		Document document = getDocument(clonedInputStream);
		List<ConversionIssue> errors = new ArrayList<>();
		BG0000Invoice invoice = sut.applyOne2OneTransformationsBasedOnMapping(document, errors).getResult();
		return sut.applyMany2OneTransformationsBasedOnMapping(invoice, document, errors);
	}

	public Document getDocument(InputStream sourceInvoiceStream) throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		saxBuilder.setIgnoringBoundaryWhitespace(true);
		return saxBuilder.build(sourceInvoiceStream);
	}

	static class MyCiiToCenConverter extends Cii2Cen {

		public MyCiiToCenConverter(Reflections reflections, EigorConfiguration configuration) throws ConfigurationException {
			super(reflections, configuration);
		}

		@Override public ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
			return super.applyOne2OneTransformationsBasedOnMapping(document, errors);
		}

		@Override public ConversionResult<BG0000Invoice> applyMany2OneTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
			return super.applyMany2OneTransformationsBasedOnMapping(partialInvoice, document, errors);
		}
	}

}
