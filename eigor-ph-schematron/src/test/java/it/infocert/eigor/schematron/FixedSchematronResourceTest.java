package it.infocert.eigor.schematron;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.xslt.SchematronResourceSCH;
import org.junit.Test;
import org.oclc.purl.dsdl.svrl.ActivePattern;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRule;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import javax.xml.transform.stream.StreamSource;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FixedSchematronResourceTest {

    SchematronResourceSCH referenceSch = SchematronResourceSCH.fromClassPath("/dogs/dogs.sch");
    ISchematronResource sutSch = new FixedSchematronResource(referenceSch);

    @Test public void inCaseOfSingleAsserionErrorItShouldBeTheSameThanTheOriginalOne() throws Exception {

        // when
        List<Object> referenceFiredAndFailed = referenceSch.applySchematronValidationToSVRL(new StreamSource(getClass().getResourceAsStream("/dogs/dogs-with-single-error.xml"))).getActivePatternAndFiredRuleAndFailedAssert();
        List<Object> sutFiredAndFailed = sutSch.applySchematronValidationToSVRL(new StreamSource(getClass().getResourceAsStream("/dogs/dogs-with-single-error.xml"))).getActivePatternAndFiredRuleAndFailedAssert();

        // then
        ActivePattern referenceActivePattern = (ActivePattern) referenceFiredAndFailed.get(0);
        ActivePattern sutActivePattern = (ActivePattern) sutFiredAndFailed.get(0);
        assertEquals(referenceActivePattern.getDocument(), sutActivePattern.getDocument());
        assertEquals(referenceActivePattern.getId(), sutActivePattern.getId());
        assertEquals(referenceActivePattern.getName(), sutActivePattern.getName());
        assertEquals(referenceActivePattern.getRole(), sutActivePattern.getRole());

        FiredRule referenceFiredRule = (FiredRule) referenceFiredAndFailed.get(1);
        FiredRule sutFiredRule = (FiredRule) sutFiredAndFailed.get(1);
        assertEquals( referenceFiredRule.getId(), sutFiredRule.getId() );

    }

    @Test public void shouldReportTheRightIds() throws Exception {

        // when
        SchematronOutputType output = sutSch.applySchematronValidationToSVRL(new StreamSource(getClass().getResourceAsStream("/dogs/dogs-with-sex-errors.xml")));
        List<Object> firedAndFailed = output.getActivePatternAndFiredRuleAndFailedAssert();

        // then
        checkIsActivePattern(firedAndFailed, 0);
        checkIsFiredRuleWithId(firedAndFailed, 1, "check-sex-is-defined");
        checkIsFiredRuleWithId(firedAndFailed, 2, "check-sex-is-defined");
        checkIsFiredRuleWithId(firedAndFailed, 3, "check-sex-is-defined");
        checkIsFailedAssert(firedAndFailed, 4, null);
        checkIsFiredRuleWithId(firedAndFailed, 5, "check-sex-is-defined");
        checkIsFailedAssert(firedAndFailed, 6, null);
        assertEquals(7, firedAndFailed.size());


    }


    @Test public void shouldReportTheSameValidity() {

        // then
        assertEquals( referenceSch.isValidSchematron(), sutSch.isValidSchematron() );

    }

    private void checkIsActivePattern(List<Object> firedAndFailed, int itemIndex) {
        assertThat( firedAndFailed.get(itemIndex), instanceOf(ActivePattern.class) );
    }

    private void checkIsFiredRuleWithId(List<Object> firedAndFailed, int itemIndex, String expectedId) {
        assertThat( firedAndFailed.get(itemIndex), instanceOf(FiredRule.class) );
        FiredRule fr = (FiredRule)firedAndFailed.get(itemIndex);
        assertEquals(expectedId, fr.getId());
    }

    private void checkIsFailedAssert(List<Object> firedAndFailed, int itemIndex, String expectedId) {
        assertThat( firedAndFailed.get(itemIndex), instanceOf(FailedAssert.class) );
        FailedAssert fr = (FailedAssert)firedAndFailed.get(itemIndex);
        assertEquals(expectedId, fr.getId());
    }


}
