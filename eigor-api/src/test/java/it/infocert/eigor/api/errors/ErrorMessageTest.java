package it.infocert.eigor.api.errors;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ErrorMessageTest {

    @Test
    public void createSimpleErrorMessage() throws Exception {
        String message = "This is a message!";
        ErrorMessage rootMessage = new ErrorMessage("I'm the first message!", "1", "10", "99");
        String exceptionMessage = "I'm an exception!";
        RuntimeException relatedException = new RuntimeException(exceptionMessage);

        List<ErrorMessage> errorMessages = Arrays.asList(
                new ErrorMessage(message, "1", "10", "99"),
                new ErrorMessage(message, ErrorCodes.TO_CEN_SCHEMATRON_ERROR),
                new ErrorMessage(relatedException, message, "1", "10", "99"),
                new ErrorMessage(relatedException, message, ErrorCodes.TO_CEN_SCHEMATRON_ERROR),
                new ErrorMessage(rootMessage, message),
                new ErrorMessage(rootMessage, relatedException, message)
        );

        for (ErrorMessage errorMessage : errorMessages) {
            assertEquals(message, errorMessage.getMessage());
            assertEquals(ErrorCodes.TO_CEN_SCHEMATRON_ERROR, errorMessage.getErrorCode());
            if (errorMessage.hasRelatedExceptions()) {
                assertEquals(relatedException, errorMessage.getRelatedException(0));
            }
        }

    }


    @Test
    public void toStringTest() throws Exception {
        String message = "I'm the first message!";
        ErrorMessage errorMessage = new ErrorMessage(message, "1", "10", "99");

        assertEquals("11099 - " + message, errorMessage.toString());
    }
}