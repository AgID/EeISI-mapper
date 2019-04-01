package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class Cii2CenTest {

	private static final Logger log = LoggerFactory.getLogger(Cii2CenTest.class);

	static Cii2Cen sut;

	@BeforeClass
	public static void setUp() throws ConfigurationException {
		EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();
		sut = new Cii2Cen(new JavaReflections(), configuration);
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
