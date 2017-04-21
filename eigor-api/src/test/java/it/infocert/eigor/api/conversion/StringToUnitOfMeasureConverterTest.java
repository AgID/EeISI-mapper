package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.StringToUnitOfMeasureConverter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringToUnitOfMeasureConverterTest {

    @Test public void shouldFindByDescription() {
        assertThat( new StringToUnitOfMeasureConverter().convert("Each"), is(UnitOfMeasureCodes.EACH_EA) );
    }

}