package it.infocert.eigor.api.utils;

import it.infocert.eigor.model.core.model.BTBG;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class JavaReflectionsTest {

    @Test
    public void getSubTypesOfBtBg() throws Exception {

        // given
        ReflectionsReflections reference = new ReflectionsReflections( new Reflections("it.infocert") );
        Set<Class<? extends BTBG>> referenceResults = reference.getSubTypesOf(BTBG.class);

        Set<Class<? extends BTBG>> manualResult = new JavaReflections().getSubTypesOf(BTBG.class);

        // then
        assertNotNull(manualResult);
        for (Class referenceItem : referenceResults) {
            assertThat( referenceItem + " not contained in the result", manualResult.contains(referenceItem), is(true) );
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubTypesOfOtherClasses() throws Exception {
        new JavaReflections().getSubTypesOf(String.class);
    }


}