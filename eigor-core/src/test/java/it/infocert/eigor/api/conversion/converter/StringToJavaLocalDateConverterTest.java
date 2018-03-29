package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.converter.StringToJavaLocalDateConverter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class StringToJavaLocalDateConverterTest {

    @Test public void testDates() {
        StringToJavaLocalDateConverter sut = (StringToJavaLocalDateConverter) StringToJavaLocalDateConverter.newConverter("dd-MMM-yy");
        LocalDate convert = sut.convert("18-Jan-75");
        assertEquals( 18, convert.getDayOfMonth() );
        assertEquals( 1, convert.getMonthOfYear() );
        assertEquals( 1975, convert.getYear() );
    }

    @Test public void playingAroundWithDatesInEnglish() {
        LocalDate parsed = LocalDate.parse("18-Jan-2017", formatterFor("dd-MMM-yyyy", Locale.ENGLISH));
        assertEquals( "2017-01-18", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian() {
        LocalDate parsed = LocalDate.parse("23-gen-2017", formatterFor("dd-MMM-yyyy", Locale.ITALIAN));
        assertEquals( "2017-01-23", parsed.toString() );
    }
    
    @Test public void shouldCovertDateWithTrailingSpaces() {
        LocalDate parsed = LocalDate.parse("23-gen-2017                          ".trim(), formatterFor("dd-MMM-yyyy", Locale.ITALIAN));
        assertEquals( "2017-01-23", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian2() {
        DateTimeFormatter formatter = formatterFor("dd-MMM-yyyy", Locale.ITALIAN);
        LocalDate of = new LocalDate(2017, Calendar.JANUARY + 1, 18);
        assertEquals( "18-gen-2017", formatter.print(of));
    }

    private DateTimeFormatter formatterFor(String s) {
        Locale english = Locale.ENGLISH;
        return formatterFor(s, english);
    }

    private DateTimeFormatter formatterFor(String s, Locale english) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(s).withLocale(english);
        return dateTimeFormatter;
    }

}