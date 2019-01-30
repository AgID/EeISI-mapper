package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Untdid4461PaymentMeansCodeToItalianCodeStringTest {

	TypeConverter<Untdid4461PaymentMeansCode, String> sut = Untdid4461PaymentMeansCodeToItalianCodeString.newConverter();


	@Test public void shouldConvertCode01() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code1));
	}
	@Test public void shouldConvertCode02() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code2));
	}
	@Test public void shouldConvertCode03() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code3));
	}
	@Test public void shouldConvertCode04() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code4));
	}
	@Test public void shouldConvertCode05() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code5));
	}
	@Test public void shouldConvertCode06() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code6));
	}
	@Test public void shouldConvertCode07() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code7));
	}

	@Test public void shouldConvertCode08() throws ConversionFailedException {
		assertEquals("MP12",  sut.convert(Untdid4461PaymentMeansCode.Code8));
	}

	@Test public void shouldConvertCode09() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code9));
	}

	@Test public void shouldConvertCode10() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code10));
	}

	@Test public void shouldConvertCode11() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code11));
	}

	@Test public void shouldConvertCode12() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code12));
	}

	@Test public void shouldConvertCode13() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code13));
	}

	@Test public void shouldConvertCode14() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code14));
	}
	@Test public void shouldConvertCode15() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code15));
	}
	@Test public void shouldConvertCode16() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code16));
	}
	@Test public void shouldConvertCode17() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code17));
	}

	@Test public void shouldConvertCode18() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code18));
	} 

	@Test public void shouldConvertCode19() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code19));
	}

	@Test public void shouldConvertCode20() throws ConversionFailedException {
		assertEquals("MP02",  sut.convert(Untdid4461PaymentMeansCode.Code20));
	}

	@Test public void shouldConvertCode21() throws ConversionFailedException {
		assertEquals("MP03",  sut.convert(Untdid4461PaymentMeansCode.Code21));
	}

	@Test public void shouldConvertCode22() throws ConversionFailedException {
		assertEquals("MP02",  sut.convert(Untdid4461PaymentMeansCode.Code22));
	}

	@Test public void shouldConvertCode23() throws ConversionFailedException {
		assertEquals("MP03",  sut.convert(Untdid4461PaymentMeansCode.Code23));
	}

	@Test public void shouldConvertCode24() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code24));
	}

	@Test public void shouldConvertCode25() throws ConversionFailedException {
		assertEquals("MP02",  sut.convert(Untdid4461PaymentMeansCode.Code25));
	}

	@Test public void shouldConvertCode26() throws ConversionFailedException {
		assertEquals("MP02",  sut.convert(Untdid4461PaymentMeansCode.Code26));
	}

	@Test public void shouldConvertCode27() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code27));
	}

	@Test public void shouldConvertCode28() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code28));
	}

	@Test public void shouldConvertCode29() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code29));
	}

	@Test public void shouldConvertCode31() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code31));
	}
	@Test public void shouldConvertCode32() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code32));
	}

	@Test public void shouldConvertCode33() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code33));
	}

	@Test public void shouldConvertCode34() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code34));
	}

	@Test public void shouldConvertCode35() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code35));
	}

	@Test public void shouldConvertCode36() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code36));
	}

	@Test public void shouldConvertCode37() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code37));
	}

	@Test public void shouldConvertCode38() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code38));
	}

	@Test public void shouldConvertCode39() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code39));
	}

	@Test public void shouldConvertCode40() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code40));
	}

	@Test public void shouldConvertCode41() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code41));
	}

	@Test public void shouldConvertCode42() throws ConversionFailedException {
		assertEquals("MP17",  sut.convert(Untdid4461PaymentMeansCode.Code42));
	}

	@Test public void shouldConvertCode43() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code43));
	}
	@Test public void shouldConvertCode44() throws ConversionFailedException {
		assertEquals("MP12",  sut.convert(Untdid4461PaymentMeansCode.Code44));
	}
	@Test public void shouldConvertCode45() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code45));
	}
	@Test public void shouldConvertCode46() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code46));
	}
	@Test public void shouldConvertCode47() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code47));
	}
	@Test public void shouldConvertCode48() throws ConversionFailedException {
		assertEquals("MP08",  sut.convert(Untdid4461PaymentMeansCode.Code48));
	}
	@Test public void shouldConvertCode49() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code49));
	}
	@Test public void shouldConvertCode50() throws ConversionFailedException {
		assertEquals("MP18",  sut.convert(Untdid4461PaymentMeansCode.Code50));
	}
	@Test public void shouldConvertCode51() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code51));
	}
	@Test public void shouldConvertCode52() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code52));
	}
	@Test public void shouldConvertCode53() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code53));
	}
	@Test public void shouldConvertCode54() throws ConversionFailedException {
		assertEquals("MP08",  sut.convert(Untdid4461PaymentMeansCode.Code54));
	}
	@Test public void shouldConvertCode55() throws ConversionFailedException {
		assertEquals("MP08",  sut.convert(Untdid4461PaymentMeansCode.Code55));
	}
	@Test public void shouldConvertCode56() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code56));
	}
	@Test public void shouldConvertCode57() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code57));
	}
	@Test public void shouldConvertCode58() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code58));
	}

	@Test public void shouldConvertCode59() throws ConversionFailedException {
		assertEquals("MP19",  sut.convert(Untdid4461PaymentMeansCode.Code59));
	}

	@Test public void shouldConvertCode60() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code60));
	}

	@Test public void shouldConvertCode61() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code61));
	}

	@Test public void shouldConvertCode62() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code62));
	}

	@Test public void shouldConvertCode63() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code63));
	}

	@Test public void shouldConvertCode64() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code64));
	}

	@Test public void shouldConvertCode65() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code65));
	}

	@Test public void shouldConvertCode66() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code66));
	}

	@Test public void shouldConvertCode67() throws ConversionFailedException {
		assertEquals("MP06",  sut.convert(Untdid4461PaymentMeansCode.Code67));
	}

	@Test public void shouldConvertCode68() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code68));
	}

	@Test public void shouldConvertCode70() throws ConversionFailedException {
		assertEquals("MP12",  sut.convert(Untdid4461PaymentMeansCode.Code70));
	}

	@Test public void shouldConvertCode74() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code74));
	}

	@Test public void shouldConvertCode75() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code75));
	}

	@Test public void shouldConvertCode76() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code76));
	}

	@Test public void shouldConvertCode77() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code77));
	}

	@Test public void shouldConvertCode78() throws ConversionFailedException {
		assertEquals("MP13",  sut.convert(Untdid4461PaymentMeansCode.Code78));
	}

	@Test public void shouldConvertCode91() throws ConversionFailedException {
		assertEquals("MP03",  sut.convert(Untdid4461PaymentMeansCode.Code91));
	}

	@Test public void shouldConvertCode92() throws ConversionFailedException {
		assertEquals("MP02",  sut.convert(Untdid4461PaymentMeansCode.Code92));
	}
	@Test public void shouldConvertCode93() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code93));
	}
	@Test public void shouldConvertCode94() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code94));
	}
	@Test public void shouldConvertCode95() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code95));
	}
	@Test public void shouldConvertCode96() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code96));
	}
	
	@Test public void shouldConvertCode97() throws ConversionFailedException {
		assertEquals("MP22",  sut.convert(Untdid4461PaymentMeansCode.Code97));
	}
	
	@Test public void shouldConvertCodeZZZ() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.CodeZZZ));
	}
	@Test public void shouldSetMP01AsDefault() throws ConversionFailedException {
		assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code36));
	}

	@Test public void shouldConvertCode30() throws ConversionFailedException {
		assertEquals("MP05",  sut.convert(Untdid4461PaymentMeansCode.Code30));
	}

	@Test(expected = ConversionFailedException.class) public void shouldNotConvertNull() throws ConversionFailedException {
		sut.convert(null);
	}

}