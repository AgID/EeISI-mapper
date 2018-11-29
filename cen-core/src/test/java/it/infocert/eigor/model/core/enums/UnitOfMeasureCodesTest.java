package it.infocert.eigor.model.core.enums;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UnitOfMeasureCodesTest {

    @Test
    public void CodeC62ShouldBeIncluded() {
        assertNotNull( UnitOfMeasureCodes.C62_ONE );
    }

    @Test
    public void CodesForMhoShouldBeIncluded() {
        assertNotNull( UnitOfMeasureCodes.MHO_NQ );
        assertNotNull( UnitOfMeasureCodes.MICROMHO_NR );
    }

}
