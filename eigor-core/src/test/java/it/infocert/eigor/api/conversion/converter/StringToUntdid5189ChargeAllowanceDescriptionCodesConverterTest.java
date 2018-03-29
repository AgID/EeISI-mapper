package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToUntdid5189ChargeAllowanceDescriptionCodesConverter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class StringToUntdid5189ChargeAllowanceDescriptionCodesConverterTest {

    @Test
    public void shouldConvertCodeIntoEnum() throws ConversionFailedException {

        StringToUntdid5189ChargeAllowanceDescriptionCodesConverter sut = (StringToUntdid5189ChargeAllowanceDescriptionCodesConverter) StringToUntdid5189ChargeAllowanceDescriptionCodesConverter.newConverter();
        Untdid5189ChargeAllowanceDescriptionCodes converted = sut.convert("45");
        assertThat( converted, equalTo( Untdid5189ChargeAllowanceDescriptionCodes.Code45 ) );

    }

}