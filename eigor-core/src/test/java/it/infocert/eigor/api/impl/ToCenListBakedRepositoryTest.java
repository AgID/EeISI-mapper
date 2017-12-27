package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ToCenListBakedRepositoryTest {

    private static IReflections reflections;
    private static ToCenListBakedRepository sut;

    @BeforeClass
    public static void setUp() throws Exception {
        reflections = new JavaReflections();

        sut = new ToCenListBakedRepository(
                new FakeToCenConversion(reflections, mock(EigorConfiguration.class))
        );
    }

    @Test public void shouldFindToCen() {

        // given
        Class<? extends ToCenConversion> aConversionThatShouldBeFound = FakeToCenConversion.class;

        // when
        ToCenConversion conversion = sut.findConversionToCen("fake");

        // then
        assertThat( conversion, notNullValue() );
        assertThat( conversion, instanceOf(aConversionThatShouldBeFound));

    }

    @Test public void shouldReturnAllSupportedFormats() {

        // when
        Set<String> formats = sut.supportedToCenFormats();

        // then
        assertThat( formats, hasItem("fake") );


    }

}