package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StringToJavaLocalDateConverter implements TypeConverter<String, LocalDate> {

    private final DateTimeFormatter formatter;

    public StringToJavaLocalDateConverter() {
        this("yyyy-MM-dd");
    }

    public StringToJavaLocalDateConverter(String pattern) {
        this(DateTimeFormat.forPattern(pattern));
    }

    public StringToJavaLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        formatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(String s) {
        return LocalDate.parse(s, formatter);
    }
}
