package it.infocert.eigor.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConversionRepositoryTest {

    @Mock private ToCenConversion toCenConversion1;
    @Mock private FromCenConversion fromCenConversion1;

    @Test
    public void shouldDelegateToConverters() {

        // given
        ConversionRepository sut = new ConversionRepository.Builder()
                .register(toCenConversion1)
                .register(fromCenConversion1)
                .build();

        when(toCenConversion1.getSupportedFormats()).thenReturn(new HashSet<>(Arrays.asList("f1", "f2")));
        when(fromCenConversion1.getSupportedFormats()).thenReturn(new HashSet<>(Arrays.asList("f3", "f4")));

        // then
        Assert.assertThat(sut.supportedFromCenFormats(), org.hamcrest.Matchers.hasItems("f3", "f4"));
        Assert.assertThat(sut.supportedToCenFormats(), org.hamcrest.Matchers.hasItems("f1", "f2"));
    }

}