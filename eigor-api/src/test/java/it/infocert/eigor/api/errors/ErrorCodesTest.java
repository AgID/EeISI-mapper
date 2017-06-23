package it.infocert.eigor.api.errors;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorCodesTest {

    @Test
    public void findRightEnumFromParameters() throws Exception {
        ErrorCodes toCenSchematronError = ErrorCodes.retrieveErrorCode("1", "10", "99");
        ErrorCodes toCenXsdError = ErrorCodes.retrieveErrorCode("1", "10", "77");

        assertEquals(ErrorCodes.TO_CEN_SCHEMATRON_ERROR, toCenSchematronError);
        assertEquals(ErrorCodes.TO_CEN_XSD_ERROR, toCenXsdError);
    }
}