package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class StringToUnitOfMeasureConverterTest {

    TypeConverter<String, UnitOfMeasureCodes> sut = StringToUnitOfMeasureConverter.newConverter();

    @Test public void shouldFindByDescription() throws ConversionFailedException {
        assertThat(sut.convert("Each"),
                is(UnitOfMeasureCodes.EACH_EA) );
        assertThat(sut.convert("One"),
                is(UnitOfMeasureCodes.C62_ONE));
    }

    @Test public void shouldConvertToC62WhenUnitIsUnknown() {

        try{
            assertThat(sut.convert("-DOES-NOT-EXIST-"),
                    is(UnitOfMeasureCodes.C62_ONE));
        }catch(ConversionFailedException cfe){

        }catch (Exception e){
            fail("Wrong exception: " + e);
        }

    }

}
