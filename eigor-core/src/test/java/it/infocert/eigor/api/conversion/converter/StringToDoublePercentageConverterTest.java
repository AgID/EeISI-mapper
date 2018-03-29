package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToDoublePercentageConverter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class StringToDoublePercentageConverterTest {

    @Test
    public void shouldConvertPercentageToDoubles() throws ConversionFailedException {
        final StringToDoublePercentageConverter converter = (StringToDoublePercentageConverter) StringToDoublePercentageConverter.newConverter();
        assertThat( converter.convert("25%"), closeTo(0.25, 0.00001) );
    }

}