package it.infocert.eigor.model.core.model.structure;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class BtBgNameTest {

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