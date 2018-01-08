package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class StringToJavaLocalDateConverter extends FromStringTypeConverter<LocalDate> {

    private final DateTimeFormatter formatter;

    public static TypeConverter<String, LocalDate> newConverter(String format) {
        return new StringToJavaLocalDateConverter(format);
    }

    public static TypeConverter<String, LocalDate> newConverter() {
        return new StringToJavaLocalDateConverter();
    }

    StringToJavaLocalDateConverter() {
        this("yyyy-MM-dd");
    }

    StringToJavaLocalDateConverter(String pattern) {
        this(DateTimeFormat.forPattern(pattern).withLocale(Locale.ENGLISH));
    }

    StringToJavaLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        formatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(String s) {
        return LocalDate.parse(s.trim(), formatter);
    }

    @Override
    public Class<LocalDate> getTargetClass() {
        return LocalDate.class;
    }


}
