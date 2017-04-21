package it.infocert.eigor.api.conversion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToJavaLocalDateConverter implements TypeConverter<String, LocalDate> {

    private final DateTimeFormatter formatter;

    public StringToJavaLocalDateConverter() {
        this("yyyy-MM-dd");
    }

    public StringToJavaLocalDateConverter(String pattern) {
        this(DateTimeFormatter.ofPattern(pattern));
    }

    public StringToJavaLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        formatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(String s) {
        return LocalDate.parse(s, formatter);
    }
}
