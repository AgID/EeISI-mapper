package it.infocert.eigor.api.errors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorCodeTest {

    @Test
    public void testEquality() {
        final ErrorCode one = new ErrorCode(ErrorCode.Location.FATTPA_OUT, ErrorCode.Action.SCH_VALIDATION, ErrorCode.Error.INVALID);
        final ErrorCode two = new ErrorCode(ErrorCode.Location.FATTPA_OUT, ErrorCode.Action.SCH_VALIDATION, ErrorCode.Error.INVALID);

        assertEquals(one, two);
    }
}
