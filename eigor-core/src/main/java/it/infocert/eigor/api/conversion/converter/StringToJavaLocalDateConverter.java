package it.infocert.eigor.api.conversion.converter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Pattern;

public class StringToJavaLocalDateConverter extends FromStringTypeConverter<LocalDate> {

    private final DateTimeFormatter formatter;
    private final Pattern regex = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{2}:[0-9]{2}$");

    public static TypeConverter<String, LocalDate> newConverter(String format) {
        return new StringToJavaLocalDateConverter(format);
    }

    public static TypeConverter<String, LocalDate> newConverter() {
        return new StringToJavaLocalDateConverter();
    }

    private StringToJavaLocalDateConverter() {
        this("yyyy-MM-dd");
    }

    private StringToJavaLocalDateConverter(String pattern) {
        this(DateTimeFormat.forPattern(pattern).withLocale(Locale.ENGLISH));
    }

    private StringToJavaLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        formatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(String s) {
        if(regex.matcher(s).matches()) {
            return LocalDate.parse(s.trim().substring(0,10), formatter);
        }
        return LocalDate.parse(s.trim(), formatter);
    }

    @Override
    public Class<LocalDate> getTargetClass() {
        return LocalDate.class;
    }


}
