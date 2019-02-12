package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianCodeStringToUntdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ItalianCodeStringToUntdid4461PaymentMeansCodeTest {


	TypeConverter<String, Untdid4461PaymentMeansCode> sut =ItalianCodeStringToUntdid4461PaymentMeansCode.newConverter();

	@Test public void shouldConvertMP01() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code10,  sut.convert("MP01"));
	}
	
	@Test public void shouldConvertMP02() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code20,  sut.convert("MP02"));
	}
	
	@Test public void shouldConvertMP03() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code23,  sut.convert("MP03"));
	}
	
	@Test public void shouldConvertMP04() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code9,  sut.convert("MP04"));
	}
	
	@Test public void shouldConvertMP05() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code30,  sut.convert("MP05"));
	}
	
	@Test public void shouldConvertMP06() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code60,  sut.convert("MP06"));
	}
	
	@Test public void shouldConvertMP07() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code49,  sut.convert("MP07"));
	}
	
	@Test public void shouldConvertMP08() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code48,  sut.convert("MP08"));
	}
	
	@Test public void shouldConvertMP09() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code46,  sut.convert("MP09"));
	}
	
	@Test public void shouldConvertMP10() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code46,  sut.convert("MP10"));
	}
	
	@Test public void shouldConvertMP11() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code46,  sut.convert("MP11"));
	}

	@Test public void shouldConvertMP12() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code70,  sut.convert("MP12"));
	}

	@Test public void shouldConvertMP13() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code70,  sut.convert("MP13"));
	}

	@Test public void shouldConvertMP14() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.CodeZZZ,  sut.convert("MP14"));
	}

	@Test public void shouldConvertMP15() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code15,  sut.convert("MP15"));
	}

	@Test public void shouldConvertMP16() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code49,  sut.convert("MP16"));
	}

	@Test public void shouldConvertMP17() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code42,  sut.convert("MP17"));
	}

	@Test public void shouldConvertMP18() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code50,  sut.convert("MP18"));
	}

	@Test public void shouldConvertMP19() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code51,  sut.convert("MP19"));
	}

	@Test public void shouldConvertMP20() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code51,  sut.convert("MP20"));
	}

	@Test public void shouldConvertMP21() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code51,  sut.convert("MP21"));
	}

	@Test public void shouldConvertMP22() throws ConversionFailedException {
		assertEquals(Untdid4461PaymentMeansCode.Code97,  sut.convert("MP22"));
	}

}
