package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.StringToIso31661CountryCodesConverter;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class ConversionRegistryTest {

    @Test
    public void shouldConvertUsingRegisteredConverters() {

        // given
        ConversionRegistry sut = new ConversionRegistry(
                new StringToIso31661CountryCodesConverter(),
                new StringToJavaLocalDateConverter()
        );

        // then
        assertEquals( Iso31661CountryCodes.HR, sut.convert(String.class, Iso31661CountryCodes.class, "HR") );
        assertEquals( LocalDate.parse("2010-12-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")), sut.convert(String.class, LocalDate.class, "2010-12-31") );

    }

    @Test
    public void shouldIgnoreTransformationsThatTrowsExceptions() {

        // given
        ConversionRegistry sut = new ConversionRegistry(
                new MockStringToNumber().alwaysFailWith(new RuntimeException()),
                new MockStringToNumber().alwaysFailWith(new RuntimeException()),
                new MockStringToNumber()
        );

        // then
        assertEquals( 13, sut.convert(String.class, Number.class, "13") );

    }

    public static class MockStringToNumber implements TypeConverter<String, Number> {

        private RuntimeException alwaysFail;

        @Override public Number convert(String s) {
            if(alwaysFail!=null) throw alwaysFail;
            return Integer.parseInt(s);
        }

        MockStringToNumber alwaysFailWith(RuntimeException e){
            this.alwaysFail = e;
            return this;
        }
    }

}