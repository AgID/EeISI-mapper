package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConversionRegistryTest {

    @Test
    public void understanding() {
        Class<Number> numberClass = Number.class;
        Class<Integer> integerClass = Integer.class;
        Assert.assertTrue( numberClass.isAssignableFrom(integerClass) );
        Assert.assertFalse( integerClass.isAssignableFrom(numberClass) );
    }

    @Test
    public void shouldNotConvertAStringToADoubleIfAStringIsNeeded() {

        // given
        ConversionRegistry sut = new ConversionRegistry(
                new StringToDoubleConverter()
        );

        // when
        IllegalArgumentException ex = null;
        Class<String> sourceClz = String.class;
        Class<String> targetClz = String.class;
        try {
            String converted = sut.convert(sourceClz, targetClz, "20100");
        }catch(IllegalArgumentException iae){
            ex = iae;
        }catch(Exception e){
            fail();
        }

        // then
        assertThat( ex.getMessage(), is("Cannot convert value '20100' of declared type 'String' to the desired type 'String'.") );

    }


    @Test
    public void shouldConvertUsingRegisteredConverters() {

        // given
        ConversionRegistry sut = new ConversionRegistry(
                new CountryNameToIso31661CountryCodeConverter(),
                new StringToJavaLocalDateConverter(),
                new LookUpEnumConversion(Iso31661CountryCodes.class)
        );

        // then
        assertEquals( Iso31661CountryCodes.HR, sut.convert(String.class, Iso31661CountryCodes.class, "HR") );
        assertEquals( LocalDate.parse("2010-12-31", DateTimeFormat.forPattern("yyyy-MM-dd")), sut.convert(String.class, LocalDate.class, "2010-12-31") );

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