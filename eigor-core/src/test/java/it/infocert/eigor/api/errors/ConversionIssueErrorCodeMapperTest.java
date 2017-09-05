package it.infocert.eigor.api.errors;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.EigorException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConversionIssueErrorCodeMapperTest {

    private List<IConversionIssue> issues;
    private ConversionIssue testIssue;

    @Before
    public void setUp() throws Exception {
        issues = createIssueList();
        testIssue = ConversionIssue.newError(new EigorException(ErrorMessage.builder().message("Test error").action("UpdateList").error("Test").build()));
    }

    @Test
    public void shouldUpdateMissingFieldsFromList() throws Exception {
        List<IConversionIssue> result = createMapper().mapAll(issues);

        for (IConversionIssue issue : result) {
            assertErrorCode(issue);
        }
    }

    @Test
    public void shouldUpdateMissingFields() throws Exception {
        IConversionIssue result = createMapper().map(testIssue);
        assertErrorCode(result);
    }

    @Test
    public void shoudlUpdateWithNewErrorMessageIfMissingFromIssue() throws Exception {
        IConversionIssue issue = new ConversionIssueErrorCodeMapper("MapperTest", "Update").map(ConversionIssue.newError(new IllegalArgumentException("Test"), "Test message"));
        ErrorCode sut = issue.getErrorMessage().getErrorCode();

        assertNotNull(sut);
        assertNotNull(sut.getLocation());
        assertNotNull(sut.getAction());
        assertNotNull(sut.getError());

        String expectedCode = "MapperTest.Update.IllegalArgument";
        assertEquals(expectedCode, sut.toString());
        assertEquals(expectedCode + " - Test message", issue.getMessage());
    }

    @Test
    public void shouldUpdateBySideEffect() throws Exception {
        new ConversionIssueErrorCodeMapper("MapperTest").mapAll(issues);

        for (IConversionIssue issue : issues) {
            assertErrorCode(issue);
        }
    }

    private void assertErrorCode(IConversionIssue issue) {
        assertEquals("MapperTest.UpdateList.Eigor", issue.getErrorMessage().getErrorCode().toString());
    }

    private ConversionIssueErrorCodeMapper createMapper() {
        return new ConversionIssueErrorCodeMapper("MapperTest");
    }

    private List<IConversionIssue> createIssueList() {
        ArrayList<IConversionIssue> list = new ArrayList<IConversionIssue>();
        for (int i = 0; i < 10; i++) {
            list.add(ConversionIssue.newError(new EigorException(ErrorMessage.builder().message("Test error").action("UpdateList").error("Test").build())));
        }
        return list;
    }
}