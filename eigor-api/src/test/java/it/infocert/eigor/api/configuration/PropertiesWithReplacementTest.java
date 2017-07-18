package it.infocert.eigor.api.configuration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PropertiesWithReplacementTest {

    Properties properties;
    PropertiesWithReplacement sut;

    @Before
    public void setUp() {
        properties = new Properties();
        sut = new PropertiesWithReplacement(properties);

        System.setProperty("alphabet","abc");
    }

    @After
    public void tearDown() {
        System.getProperties().remove("alphabet");
    }

    @Test
    public void shouldNotReplaceUnexistingEnvVariables() {

        // given
        String aPlaceholderForAnEnvVariableThatDoesNotExistForSure = "${env." + System.getenv().keySet().toString() + "}";
        properties.setProperty("theEnv", aPlaceholderForAnEnvVariableThatDoesNotExistForSure);

        // when
        String theEnv = sut.getProperty("theEnv");

        // then
        assertThat(theEnv, nullValue());

    }


    @Test
    public void shouldReplaceSystemPropertiesInTheMiddle() {

        // given
        properties.setProperty("theProp", "This string '${prop.alphabet}' is the value.");

        // when
        String path = sut.getProperty("theProp");

        // then
        assertThat( path, is("This string 'abc' is the value.") );

    }

    @Test
    public void shouldReplaceEnvVariablesInTheMiddle() {

        // given
        Map.Entry<String, String> anEnvVariable = System.getenv().entrySet().iterator().next();
        properties.setProperty("theEnv", "This string '${env." + anEnvVariable.getKey() + "}' is the value.");

        // when
        String path = sut.getProperty("theEnv");

        // then
        assertThat( path, is("This string '" + anEnvVariable.getValue() + "' is the value.") );

    }

    @Test
    public void shouldReplaceEnvVariables() {

        // given
        Map.Entry<String, String> anEnvVariable = System.getenv().entrySet().iterator().next();
        properties.setProperty("theEnv", "${env." + anEnvVariable.getKey() + "}");

        // when
        String path = sut.getProperty("theEnv");

        // then
        assertThat( path, is(anEnvVariable.getValue()) );

    }

    @Test(timeout = 1000)
    public void shouldDetectLoops() {

        // given
        properties.setProperty("a", "${b}");
        properties.setProperty("b", "${c}");
        properties.setProperty("c", "${a}");

        // when
        IllegalStateException iae = null;
        try {
            String greetings = sut.getProperty("b");
            fail();
        }catch(IllegalStateException e){
            iae = e;
        }

        // then
        assertThat(iae.getMessage(), is("Circular reference detected on placeholders ${b} -> ${c} -> ${a} -> ${b}.") );

    }

    @Test
    public void shouldWorkRecursively() {

        // given
        properties.setProperty("char1", "A${char2}");
        properties.setProperty("char2", "B${char3}");
        properties.setProperty("char3", "C");
        properties.setProperty("alphabet", "The alphabet is: ${char1}");

        // when
        String greetings = sut.getProperty("alphabet");

        // then
        assertThat( greetings, is("The alphabet is: ABC") );

    }

    @Test
    public void shouldReplaceOtherProperties() {

        // given
        properties.setProperty("param.1", "Hello");
        properties.setProperty("param.2", "World");
        properties.setProperty("greetings", "${param.1} ${param.2}");

        // when
        String greetings = sut.getProperty("greetings");

        // then
        assertThat( greetings, is("Hello World") );

    }

}

