package it.infocert.eigor.converter.ubl2cen;

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

public class Ubl2CenTest {

	private static final Logger log = LoggerFactory.getLogger(Ubl2CenTest.class);

	private Ubl2Cen sut;

	@Before
	public void setUp() throws ConfigurationException {
		EigorConfiguration conf = new PropertiesBackedConfiguration()
                .addProperty("eigor.workdir", "classpath:")
                .addProperty("eigor.converter.ubl-cen.mapping.one-to-one", "converterdata/converter-ubl-cen/mappings/one_to_one.properties")
				.addProperty("eigor.converter.ubl-cen.mapping.many-to-one", "converterdata/converter-ubl-cen/mappings/many_to_one.properties")
				.addProperty("eigor.converter.ubl-cen.mapping.one-to-many", "converterdata/converter-ubl-cen/mappings/one_to_many.properties")
				.addProperty("eigor.converter.ubl-cen.xsd", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ubl/xsd/UBL-Invoice-2.1.xsd")
				.addProperty("eigor.converter.ubl-cen.schematron", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt")
				.addProperty("eigor.converter.ubl-cen.schematron.auto-update-xslt", "false")
				.addProperty("eigor.converter.ubl-cen.mapping.custom", "converterdata/converter-ubl-cen/mappings/custom.conf")
				.addProperty("eigor.converter.ubl-cen.cius", "file:../converter-commons/src/main/resources/converterdata/converter-commons/ubl/cius/schematron-xslt/EN16931-CIUS-IT-UBLValidation.xslt")
				.addProperty("eigor.converter.ubl-cen.cius.auto-update-xslt", "false")
				;
		sut = new Ubl2Cen(new JavaReflections(), conf);
		sut.configure();
	}

	@Test
	public void shouldSupportUbl() {
		assertThat(sut.support("ubl"), is(true));
	}

	@Test
	public void shouldNotSupportUbl() {
		assertThat(sut.support("fake"), is(false));
	}

	@Test
	public void shouldSupportedFormatsUbl() {
		assertThat(sut.getSupportedFormats(), contains("ubl"));
	}

	@Test
	public void testNullFormat() {
		assertFalse(sut.support(null));
	}

}
