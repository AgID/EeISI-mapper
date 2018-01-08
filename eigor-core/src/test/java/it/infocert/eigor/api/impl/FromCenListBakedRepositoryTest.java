package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FromCenListBakedRepositoryTest {

    private static IReflections reflections;
    private static FromCenListBakedRepository sut;

    @BeforeClass
    public static void setUp() {
        reflections = new JavaReflections();

        sut = new FromCenListBakedRepository(
                new FakeFromCenConversion(reflections, mock(EigorConfiguration.class))
        );
    }

    @Test public void shouldFindFromCen() {

        // given
        Class<? extends FromCenConversion> aConversionThatShouldBeFound = FakeFromCenConversion.class;

        // when
        FromCenConversion conversion = sut.findConversionFromCen("fake");

        // then
        assertThat( conversion, notNullValue() );
        assertThat( conversion, instanceOf(aConversionThatShouldBeFound));

    }



}