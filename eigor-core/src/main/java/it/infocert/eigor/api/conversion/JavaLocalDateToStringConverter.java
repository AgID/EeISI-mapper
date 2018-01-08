package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;

public class JavaLocalDateToStringConverter extends ToStringTypeConverter<LocalDate> {

    private final String pattern;

    public static TypeConverter<LocalDate, String> newConverter(){
        return new JavaLocalDateToStringConverter();
    }

    public static TypeConverter<LocalDate, String> newConverter(String pattern){
        return new JavaLocalDateToStringConverter(pattern);
    }

    JavaLocalDateToStringConverter() {
        pattern = "yyyy-MM-dd";
    }

    JavaLocalDateToStringConverter(String pattern) {
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
