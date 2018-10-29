package it.infocert.eigor.api.conversion.converter;


import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;


public class FilteringEnumConversionTest {

    @Test
    public void shouldUseTheProperSourceAndDestinationClasses() {

        // given
        FilteringEnumConversion<Integer, Untdid1001InvoiceTypeCode> sut = new FilteringEnumConversion<Integer, Untdid1001InvoiceTypeCode>(Untdid1001InvoiceTypeCode.class) {

            @Override
            protected Filter<Untdid1001InvoiceTypeCode> buildFilter(Integer value) {
                return null;
            }

            @Override
            public Class<Integer> getSourceClass() {
                return null;
            }
        };

        // then
        Class targetClass = sut.getTargetClass();
        Assert.assertThat(targetClass, equalTo( (Class)Untdid1001InvoiceTypeCode.class) );

    }

}
