package it.infocert.eigor.api.errors;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import static it.infocert.eigor.api.errors.ErrorCode.*;

import static org.junit.Assert.assertEquals;

public class ErrorMessageTest {

    @Test
    public void createSimpleErrorMessage() throws Exception {
        String message = "This is a message!";
        ErrorMessage rootMessage = new ErrorMessage("I'm the first message!", Location.FATTPA_IN, Action.SCH_VALIDATION, ErrorCode.Error.INVALID);
        String exceptionMessage = "I'm an exception!";
        RuntimeException relatedException = new RuntimeException(exceptionMessage);

        List<ErrorMessage> errorMessages = Arrays.asList(
                new ErrorMessage(message, Location.FATTPA_IN, Action.SCH_VALIDATION, ErrorCode.Error.INVALID),
                new ErrorMessage(relatedException, message, new ErrorCode(Location.FATTPA_IN, Action.SCH_VALIDATION, ErrorCode.Error.INVALID)),
                new ErrorMessage(rootMessage, message),
                new ErrorMessage(rootMessage, relatedException, message)
        );

        for (ErrorMessage errorMessage : errorMessages) {
            assertEquals(message, errorMessage.getMessage());
            assertEquals(new ErrorCode(Location.FATTPA_IN, Action.SCH_VALIDATION, ErrorCode.Error.INVALID), errorMessage.getErrorCode());
            if (errorMessage.hasRelatedExceptions()) {
                assertEquals(relatedException, errorMessage.getRelatedException(0));
            }
        }

    }


    @Test
    public void toStringTest() throws Exception {
        String message = "I'm the first message!";
        ErrorMessage errorMessage = new ErrorMessage(message, Location.FATTPA_IN, Action.SCH_VALIDATION, ErrorCode.Error.INVALID);

        assertEquals("FATTPA_IN.SCH_VALIDATION.INVALID - " + message, errorMessage.toString());
    }
}