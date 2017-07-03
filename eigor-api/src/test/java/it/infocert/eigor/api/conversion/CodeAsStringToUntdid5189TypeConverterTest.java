package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class CodeAsStringToUntdid5189TypeConverterTest {

    @Test
    public void shouldConvertCodeIntoEnum() {

        CodeAsStringToUntdid5189TypeConverter sut = new CodeAsStringToUntdid5189TypeConverter();
        Untdid5189ChargeAllowanceDescriptionCodes converted = sut.convert("45");
        assertThat( converted, equalTo( Untdid5189ChargeAllowanceDescriptionCodes.Code45 ) );

    }

}