package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
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
    }

    @Test public void shouldThrowAnExceptionWhenUnitIsUnknown() {

        try{
            sut.convert("-DOES-NOT-EXIST-");
            fail("Exception expected");
        }catch(ConversionFailedException cfe){

        }catch (Exception e){
            fail("Wrong exception: " + e);
        }

    }

}