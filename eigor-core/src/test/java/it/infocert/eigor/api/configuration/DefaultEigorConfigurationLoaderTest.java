package it.infocert.eigor.api.configuration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultEigorConfigurationLoaderTest {

    @Test
    public void shouldUseSensibleDefaults() {


        System.setProperty(DefaultEigorConfigurationLoader.EIGOR_CONFIGURATION_FILE_SYSTEM_PROPERTY, "classpath:alternative-eigor-test.properties");

        DefaultEigorConfigurationLoader sut = new DefaultEigorConfigurationLoader();
        EigorConfiguration conf = sut.loadConfiguration();

        assertThat(conf.getMandatoryString("xxx")).isEqualTo("yyy");
        assertThat(conf.getMandatoryString("eigor.converter.ubl-cen.mapping.one-to-one")).isEqualTo("classpath:converterdata/converter-ubl-cen/mappings/one_to_one.properties");


    }

}
