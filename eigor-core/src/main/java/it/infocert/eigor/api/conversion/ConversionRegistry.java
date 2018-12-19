package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.model.core.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class ConversionRegistry {

    public static final ConversionRegistry DEFAULT_REGISTRY = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("dd-MMM-yy"),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
            StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),
            StringToUnitOfMeasureConverter.newConverter(),
            LookUpEnumConversion.newConverter(UnitOfMeasureCodes.class),
            StringToBigDecimalConverter.newConverter(),
            StringToStringConverter.newConverter(),
            JavaLocalDateToStringConverter.newConverter(),
            JavaLocalDateToStringConverter.newConverter("dd-MMM-yy"),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            Iso31661CountryCodesToStringConverter.newConverter(),
            BigDecimalToStringConverter.newConverter ("#.00"),
            UnitOfMeasureCodesToStringConverter.newConverter()
    );

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<TypeConverter> converters;



    public ConversionRegistry(List<TypeConverter> converters) {
        this.converters = new ArrayList<>(converters);
    }

    public ConversionRegistry(TypeConverter... converters) {
        this.converters = Arrays.asList(converters);
    }

    /**
     * Converts the given value of type sourceClz, into the corresponding value of type targetClz.
     *
     * @param sourceClz The type of the value that should be converted.
     * @param targetClz The type value should be converted to.
     * @param value     The value that should be converted to targetClz.
     * @throws IllegalArgumentException When it is not able to convert the given value to the desired class.
     */
    public <T, S> T convert(Class<? extends S> sourceClz, Class<? extends T> targetClz, S value) {

        for (TypeConverter converter : converters) {

            if (value.getClass().isAssignableFrom(converter.getSourceClass())) {
                if (targetClz.isAssignableFrom(converter.getTargetClass())) {
                    log.trace("Trying to convert value '{}' with converter '{}'.", value, converter);
                    try {
                        return (T) converter.convert(value);
                    } catch (Exception e) {
                        log.trace("Skipped converter '{}' because of error.", converter, e);
                    }
                } else {
                    log.trace("Skipped converter '{}' because it convertes to '{}' of type '{}' but required target is '{}'.",
                            converter,
                            sourceClz.getName(),
                            converter.getTargetClass().getName(),
                            targetClz.getName());
                }
            } else {
                log.trace("Skipped converter '{}' because it converts from type '{}' but required source is '{}'.",
                        converter,
                        converter.getSourceClass().getName(),
                        sourceClz.getName());
            }
        }
        throw new IllegalArgumentException(
                format("Cannot convert value '%s' of declared type '%s' to the desired type '%s'.",
                        String.valueOf(value), sourceClz.getSimpleName(), targetClz.getSimpleName())
        );

    }
}
