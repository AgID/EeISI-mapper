package it.infocert.eigor.api.utils;

import it.infocert.eigor.model.core.model.BTBG;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JavaReflectionsTest {

    @Test
    public void getSubTypesOfBtBg() throws Exception {

        // given
        ReflectionsReflections reference = new ReflectionsReflections( );
        Set<Class<? extends BTBG>> referenceResults = reference.getSubTypesOfBtBg();

        Set<Class<? extends BTBG>> manualResult = new JavaReflections().getSubTypesOfBtBg();

        // then
        assertNotNull(manualResult);
        for (Class referenceItem : referenceResults) {
            assertThat( referenceItem + " not contained in the result", manualResult.contains(referenceItem), is(true) );
        }

    }

}