package it.infocert.eigor.api.configuration;

import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PropertiesBackedConfigurationTest {

    @Test
    public void shouldSupportReplacement() {

        // given
        Properties properties = new Properties();
        properties.put("greet", "Hi");
        properties.put("name", "John");
        properties.put("message", "${greet} ${name}");

        PropertiesBackedConfiguration sut = new PropertiesBackedConfiguration(properties);

        // when
        String message = sut.getMandatoryString("message");

        // then
        assertThat( message, is("Hi John") );
    }

}
