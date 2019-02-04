package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.converter.cen2fattpa.models.TipoDocumentoType;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static it.infocert.eigor.converter.cen2fattpa.models.TipoDocumentoType.*;
import static it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore("waitng for fix for TDxx")
@RunWith(Parameterized.class)
public class Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverterTest {

    final Untdid1001InvoiceTypeCode input;
    final TipoDocumentoType converted;
    static TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> sut;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Code380, TD_01 },
                { Code386, TD_02 },
                { Code381, TD_04 },
                { Code383, TD_05 },
                { Code389, TD_01 },
                { Code393, TD_01 },
                { Code6, TD_01 } // default case
        });
    }

    public Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverterTest(Untdid1001InvoiceTypeCode input, TipoDocumentoType converted) {
        this.input = input;
        this.converted = converted;
    }

    @BeforeClass
    public static void setUp() {
        sut = Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter.newConverter();
    }

    @Test
    public void map() throws ConversionFailedException {
        assertThat( sut.convert(input), is(converted) );
    }

}
