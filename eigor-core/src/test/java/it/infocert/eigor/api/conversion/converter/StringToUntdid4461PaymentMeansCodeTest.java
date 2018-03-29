package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToUntdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringToUntdid4461PaymentMeansCodeTest {

    @Test
    public void shouldConvertByCode() throws ConversionFailedException {
        StringToUntdid4461PaymentMeansCode sut = (StringToUntdid4461PaymentMeansCode) StringToUntdid4461PaymentMeansCode.newConverter();
        Untdid4461PaymentMeansCode abc = sut.convert("3");
        assertThat( abc, equalTo( Untdid4461PaymentMeansCode.Code3 ) );
    }

    @Test(expected = ConversionFailedException.class)
    public void shouldNotConvertByAnUnknownCode() throws ConversionFailedException {
        StringToUntdid4461PaymentMeansCode sut = (StringToUntdid4461PaymentMeansCode) StringToUntdid4461PaymentMeansCode.newConverter();
        Untdid4461PaymentMeansCode abc = sut.convert("ABC");
    }

}