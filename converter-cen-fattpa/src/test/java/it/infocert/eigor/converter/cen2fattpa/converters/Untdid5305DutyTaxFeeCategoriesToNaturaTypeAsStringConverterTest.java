package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import org.junit.Test;

import static org.junit.Assert.*;

public class Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverterTest {

    static Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter sut = Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter.newConverter();

    @Test
    public void convert() {
        assertEquals( null, sut.convert(Untdid5305DutyTaxFeeCategories.A) );
        assertEquals( "N3", sut.convert(Untdid5305DutyTaxFeeCategories.Z) );
    }
}
