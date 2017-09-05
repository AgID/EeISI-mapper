package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;

public class JavaLocalDateToStringConverter extends ToStringTypeConverter<LocalDate> {

    private final String pattern;

    public JavaLocalDateToStringConverter() {
        pattern = "yyyy-MM-dd";
    }

    public JavaLocalDateToStringConverter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String convert(LocalDate localDate) {
        return localDate.toString(pattern);
    }

    @Override
    public Class<LocalDate> getSourceClass() {
        return LocalDate.class;
    }
}
