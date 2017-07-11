package com.infocert.eigor.api;

import org.junit.Test;

public class EigorApiTest {

    @Test
    public void shouldCreateTheAPI() {

        // should be created
        EigorAPI api = new EigorAPI();

        // should convert
        api.convert("<ubl>ubl</ubl>".getBytes(), "ubl", "fattpa");

        // should verify
        api.verify("<ubl>ubl</ubl>".getBytes(), "ubl");

    }


}
