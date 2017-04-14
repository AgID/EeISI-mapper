package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.StringToDoublePercentageConverter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class StringToDoublePercentageConverterTest {

    @Test
    public void shouldConvertPercentageToDoubles() {
        assertThat( new StringToDoublePercentageConverter().convert("25%"), closeTo(0.25, 0.00001) );
    }

}