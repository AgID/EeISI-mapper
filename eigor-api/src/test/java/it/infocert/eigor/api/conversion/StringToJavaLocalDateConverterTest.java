package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class StringToJavaLocalDateConverterTest {

    @Test public void testDates() {
        DateTimeFormatter dateTimeFormatter = formatterFor("dd-MMM-yy");
        StringToJavaLocalDateConverter sut = new StringToJavaLocalDateConverter(dateTimeFormatter);
        LocalDate convert = sut.convert("18-Jan-75");
    }

    private DateTimeFormatter formatterFor(String s) {
        return DateTimeFormat.forPattern(s);
    }

    @Test public void playingAroundWithDatesInEnglish() {
        LocalDate parsed = LocalDate.parse("18-Jan-2017", formatterFor("dd-MMM-yyyy"));
        assertEquals( "2017-01-18", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian() {
        LocalDate parsed = LocalDate.parse("23-gen-2017", formatterFor("dd-MMM-yyyy"));
        assertEquals( "2017-01-23", parsed.toString() );
    }

    @Test public void playingAroundWithDatesInItalian2() {
        DateTimeFormatter formatter = formatterFor("dd-MMM-yyyy");
        LocalDate of = new LocalDate(2017, Calendar.JANUARY, 18);
        assertEquals( "18-gen-2017", formatter.print(of));
    }

}