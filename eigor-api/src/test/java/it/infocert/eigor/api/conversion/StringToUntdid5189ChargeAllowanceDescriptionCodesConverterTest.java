package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class StringToUntdid5189ChargeAllowanceDescriptionCodesConverterTest {

    @Test
    public void shouldConvertCodeIntoEnum() {

        StringToUntdid5189ChargeAllowanceDescriptionCodesConverter sut = new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter();
        Untdid5189ChargeAllowanceDescriptionCodes converted = sut.convert("45");
        assertThat( converted, equalTo( Untdid5189ChargeAllowanceDescriptionCodes.Code45 ) );

    }

}