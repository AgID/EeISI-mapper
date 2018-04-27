package it.infocert.eigor.converter.ublcn2cen;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class UblCn2CenTest {

	private static final Logger log = LoggerFactory.getLogger(UblCn2CenTest.class);

	private static UblCn2Cen sut;

	@BeforeClass
	public static void setUp() throws ConfigurationException {
		EigorConfiguration conf = new PropertiesBackedConfiguration()
                .addProperty("eigor.workdir", "classpath:")
                .addProperty("eigor.converter.ublcn-cen.mapping.one-to-one", "converterdata/converter-ublcn-cen/mappings/one_to_one.properties")
				.addProperty("eigor.converter.ublcn-cen.mapping.many-to-one", "converterdata/converter-ublcn-cen/mappings/many_to_one.properties")
				.addProperty("eigor.converter.ublcn-cen.mapping.one-to-many", "converterdata/converter-ublcn-cen/mappings/one_to_many.properties")
				.addProperty("eigor.converter.ublcn-cen.xsd", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsd/UBL-CreditNote-2.1.xsd")
				.addProperty("eigor.converter.ublcn-cen.schematron", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt")
				.addProperty("eigor.converter.ublcn-cen.schematron.auto-update-xslt", "false")
				.addProperty("eigor.converter.ublcn-cen.mapping.custom", "converterdata/converter-ublcn-cen/mappings/custom.conf")
				.addProperty("eigor.converter.ublcn-cen.cius", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ubl/cius/schematron-xslt/EN16931-CIUS-IT-UBLValidation.xslt")
				.addProperty("eigor.converter.ublcn-cen.cius.auto-update-xslt", "false")
				;
		sut = new UblCn2Cen(new JavaReflections(), conf);
		sut.configure();
	}

	@Test
	public void shouldSupportUblCn() {
		assertThat(sut.support("ublcn"), is(true));
	}

	@Test
	public void shouldNotSupportUblCn() {
		assertThat(sut.support("fake"), is(false));
	}

	@Test
	public void shouldSupportedFormatsUblCn() {
		assertThat(sut.getSupportedFormats(), contains("ublcn"));
	}

	@Test
	public void testNullFormat() {
		assertFalse(sut.support(null));
	}

}
