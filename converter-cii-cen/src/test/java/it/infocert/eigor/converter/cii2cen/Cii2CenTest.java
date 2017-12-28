package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class Cii2CenTest {

	private static final Logger log = LoggerFactory.getLogger(Cii2CenTest.class);

	private Cii2Cen sut;
	
	@Before
	public void setUp() throws ConfigurationException {
		EigorConfiguration conf = new PropertiesBackedConfiguration()
				.addProperty("eigor.converter.cii-cen.mapping.one-to-one", "converterdata/converter-cii-cen/mappings/one_to_one.properties")
				.addProperty("eigor.converter.cii-cen.mapping.many-to-one", "converterdata/converter-cii-cen/mappings/many_to_one.properties")
				.addProperty("eigor.converter.cii-cen.mapping.one-to-many", "converterdata/converter-cii-cen/mappings/one_to_many.properties")
				.addProperty("eigor.converter.cii-cen.xsd", "file:src/test/resources/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd")
				.addProperty("eigor.converter.cii-cen.schematron", "converterdata/converter-cii-cen/cii/schematron-xslt/EN16931-CII-validation.xslt")
				.addProperty("eigor.converter.cii-cen.mapping.custom", "converterdata/converter-cii-cen/mappings/custom.conf")
				.addProperty("eigor.converter.cii-cen.cius", "converterdata/converter-cii-cen/cius/schematron-xslt/EN16931-CIUS-IT-CIIValidation.xslt")
				;
		sut = new Cii2Cen(new JavaReflections(), conf);
		sut.configure();
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

}
