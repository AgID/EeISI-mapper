package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class StringToJavaLocalDateConverterTest {

    @Test public void testDates() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH);
        StringToJavaLocalDateConverter sut = new StringToJavaLocalDateConverter(dateTimeFormatter);
        LocalDate convert = sut.convert("18-Jan-75");
    }

    @Test public void playingAroundWithDatesInEnglish() {
        LocalDate parsed = LocalDate.parse("18-Jan-2017", DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
        assertEquals( "2017-01-18", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian() {
        LocalDate parsed = LocalDate.parse("23-gen-2017", DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ITALIAN));
        assertEquals( "2017-01-23", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ITALIAN);
        LocalDate of = LocalDate.of(2017, Month.JANUARY, 18);
        assertEquals( "18-gen-2017", formatter.format(of));
    }

}