package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.converter.Base64StringToBinaryConverter;
import it.infocert.eigor.model.core.datatypes.Binary;
import org.codehaus.plexus.util.Base64;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class Base64StringToBinaryConverterTest {

    @Test public void shouldConvertBetweenBase64AndCenBinary() throws Exception {

        testStringDecoding("Hello world");
        testStringDecoding("");
        testStringDecoding("==> STRING W|TH STRANG€ CHAR <==");

    }

    private void testStringDecoding(String stringToEncode) throws Exception {
        // given
        String base64Encoded = new String(Base64.encodeBase64(stringToEncode.getBytes()));

        // when
        Binary convert = Base64StringToBinaryConverter.newConverter().convert(base64Encoded);

        // then
        Assert.assertThat( new String( convert.getBytes() ), equalTo(stringToEncode) );
    }

}