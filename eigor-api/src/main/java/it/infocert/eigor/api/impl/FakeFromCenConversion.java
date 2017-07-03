package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A fake conversion used to lay out the API general structure.
 *
 * @see FakeToCenConversion
 */
public class FakeFromCenConversion extends AbstractFromCenConverter {

    private static final ConversionRegistry conversionRegistry = new ConversionRegistry(
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion(Iso31661CountryCodes.class),
            new StringToJavaLocalDateConverter("dd-MMM-yy"),
            new StringToJavaLocalDateConverter("yyyy-MM-dd"),
            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),
            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
            new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),
            new StringToUnitOfMeasureConverter(),
            new LookUpEnumConversion(UnitOfMeasureCodes.class),
            new StringToDoubleConverter(),
            new StringToStringConverter(),
            new JavaLocalDateToStringConverter(),
            new JavaLocalDateToStringConverter("dd-MMM-yy"),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new Iso31661CountryCodesToStringConverter(),
            new DoubleToStringConverter("#.00"),
            new UnitOfMeasureCodesToStringConverter()
    );

    public FakeFromCenConversion(Reflections reflections) {
        super(reflections, conversionRegistry);
    }

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) {
        BinaryConversionResult binaryConversionResult = new BinaryConversionResult("this is a fake invoice".getBytes(), new ArrayList<ConversionIssue>());
        return binaryConversionResult;
    }

    @Override
    public boolean support(String format) {
        return "fake".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Collections.singletonList("fake"));
    }

    @Override
    public String extension() {
        return "fake";
    }

    @Override
    public String getMappingPath() {
        return "/tmp/fake.properties";
    }
}
