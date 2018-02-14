package it.infocert.eigor.model.core.model.structure;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class BtBgNameTest {

    @Test
    public void shouldFormatDifferentNames() throws Exception {
        assertEquals("BT0001", BtBgName.formatPadded("BT-1") );
        assertEquals("BT0012", BtBgName.formatPadded("BT12"));
        assertEquals("BT0021", BtBgName.formatPadded("Bt21"));
        assertEquals("BT0023", BtBgName.formatPadded("bt 23"));
        assertEquals("BT0023", BtBgName.formatPadded("bt          23"));
        assertEquals("BG0015", BtBgName.formatPadded("bG-15"));
        assertEquals("BG0015", BtBgName.formatPadded("bg- 15"));
        assertEquals("BG0015", BtBgName.formatPadded("bg - 15"));
        assertEquals("BG0015", BtBgName.formatPadded("bg -15"));
        assertEquals("BG0015", BtBgName.formatPadded("bg                  -15"));
        assertEquals("BG0015-1", BtBgName.formatPadded("BG15-1"));
        assertEquals("BG0015-1", BtBgName.formatPadded("bg -15-1"));
        assertEquals("BG0015-1", BtBgName.formatPadded("bg -15-                      1"));
    }

    @Test
    public void shouldFailIfNotAMeaningfulBgBtName() throws Exception {
        try {
            BtBgName.formatPadded("invalid string");
            fail();
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void shouldParseSuccessfully() {

        assertExpectedParsing("bt12345", 12345, "BT");
        assertExpectedParsing("bt-12345", 12345, "BT");
        assertExpectedParsing(" bt-12345 ", 12345, "BT");
        assertExpectedParsing(" BT-12345 ", 12345, "BT");

        assertExpectedParsing("bg54321", 54321, "BG");
        assertExpectedParsing("bg-54321", 54321, "BG");
        assertExpectedParsing(" bg-54321 ", 54321, "BG");
        assertExpectedParsing(" BG-54321 ", 54321, "BG");

    }

    @Test
    public void shouldFailParsing() {

        assertFailedParsing("bt12bt345");
        assertFailedParsing("bq12");
        assertFailedParsing("bgg12");
        assertFailedParsing("bg BG");
        assertFailedParsing("BT-12BG");
        assertFailedParsing("");
        assertFailedParsing("BT-");

    }

    @Test
    public void shouldParseIntoStandardString() throws Exception {
        assertEquals("BT-1", BtBgName.formatStandardCen("BT-1") );
        assertEquals("BT-1", BtBgName.formatStandardCen("BT-0001") );
        assertEquals("BT-12", BtBgName.formatStandardCen("BT12"));
        assertEquals("BT-21", BtBgName.formatStandardCen("Bt21"));
        assertEquals("BT-23", BtBgName.formatStandardCen("bt 23"));
        assertEquals("BT-23", BtBgName.formatStandardCen("bt 00023"));
        assertEquals("BT-23", BtBgName.formatStandardCen("bt0023"));
        assertEquals("BT-23", BtBgName.formatStandardCen("bt          23"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bG-15"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bg- 15"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bg - 15"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bg -15"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bg                  -15"));
        assertEquals("BG-15", BtBgName.formatStandardCen("bg             - 0000             15"));
        assertEquals("BG-15-1", BtBgName.formatStandardCen("BG15-1"));
        assertEquals("BG-15-1", BtBgName.formatStandardCen("bg -15-1"));
        assertEquals("BG-15-1", BtBgName.formatStandardCen("bg -15-                      1"));
        assertEquals("BG-15-1", BtBgName.formatStandardCen("bg -15-                      0000000000000001"));
    }

    private void assertFailedParsing(String badFormat) {
        try{
            BtBgName.parse(badFormat);
            fail();
        }catch(IllegalArgumentException e){
            // ok
        }

    }

    private void assertExpectedParsing(String btbgAsString, int expectedNumber, String expectedBgBt) {
        BtBgName name = BtBgName.parse(btbgAsString);
        assertThat( name.bgOrBt(), is(expectedBgBt) );
        assertThat( name.number(), is(expectedNumber) );
    }

}