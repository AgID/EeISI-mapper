package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.LinePostFixSupport.CenLine;
import it.infocert.eigor.converter.cen2fattpa.LinePostFixSupport.FattpaLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class LinePostFixSupportTest {

    LinePostFixSupport sut;

    @Before
    public void setUp() {
        sut = new LinePostFixSupport();
    }

    @Test
    public void exploratory() {
        sut = new LinePostFixSupport(4);

        sut.registerForPostFix(masterLine("A"), fattpaLine());
        sut.registerForPostFix(masterLine("B"), fattpaLine());
        sut.registerForPostFix(masterLine("C"), fattpaLine());
        sut.registerForPostFix(masterLine("D"), fattpaLine());

        try {
            sut.appliesRenumbering();
            fail("Expected exception");
        }catch(IllegalStateException ise) {

        }catch(Exception e) {
            fail("unexpected exception");
        }

    }

    @Test
    public void shouldRenumberSingleLinesWithoutReusingNumbersAlreadyInPlace() {

        // given
        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();
        FattpaLine fattpaLine3 = fattpaLine();
        FattpaLine fattpaLine4 = fattpaLine();


        sut.registerForPostFix(masterLine("A-1"), fattpaLine1);
        sut.registerForPostFix(masterLine("9997"), fattpaLine2);
        sut.registerForPostFix(fattpaLine3);
        sut.registerForPostFix(fattpaLine4);

        sut.appliesRenumbering();

        verify(fattpaLine3).setNumeroLinea(9999);
        verify(fattpaLine4).setNumeroLinea(9996);

    }

    @Test
    public void shouldRenumberSingleLinesStartingfrom99999Backward() {

        // given
        FattpaLine line1 = fattpaLine();
        FattpaLine line2 = fattpaLine();
        FattpaLine line3 = fattpaLine();
        sut.registerForPostFix(line1);
        sut.registerForPostFix(line2);
        sut.registerForPostFix(line3);

        // when
        sut.appliesRenumbering();

        // then
        verify(line1).setNumeroLinea(9999);
        verify(line2).setNumeroLinea(9998);
        verify(line3).setNumeroLinea(9997);


    }

    @Test
    public void shouldNotBePossibleToRenumberLinesTwice() {

        // when
        sut.registerForPostFix(masterLine("003"), fattpaLine());
        sut.appliesRenumbering();

        try {
            sut.appliesRenumbering();
            fail("exception expected");
        }catch(IllegalStateException ise){
            // ok
        }catch(Exception e){
            fail("wrong exception");
        }

    }

    @Test
    public void shouldNotBePossibleToAddLinesAfterRenumbering() {

        // when
        sut.registerForPostFix(masterLine("003"), fattpaLine());
        sut.appliesRenumbering();

        try {
            sut.registerForPostFix(masterLine("003"), fattpaLine());
            fail("exception expected");
        }catch(IllegalStateException ise){
            // ok
        }catch(Exception e){
            fail("wrong exception");
        }

    }

    @Test
    public void shouldNotBePossibleToAddLineAfterRenumbering() {

        // when
        sut.appliesRenumbering();

        try {
            sut.registerForPostFix(fattpaLine());
            fail("exception expected");
        }catch(IllegalStateException ise){
            // ok
        }catch(Exception e){
            fail("wrong exception");
        }

    }

    @Test
    public void shouldReplaceANotNumberWithANumberBiggerThanTheMaximumNumberUsed() {

        // given
        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();
        FattpaLine fattpaLine3 = fattpaLine();

        sut.registerForPostFix(masterLine("003"), fattpaLine1);
        sut.registerForPostFix(masterLine("A-1"), fattpaLine2);
        sut.registerForPostFix(masterLine("004"), fattpaLine3);

        sut.appliesRenumbering();

        verify(fattpaLine1).setNumeroLinea(3);
        verify(fattpaLine2).setNumeroLinea(5);
        verify(fattpaLine3).setNumeroLinea(4);
    }

    @Test
    public void shouldReplace10000WithTheFirstNumberAvailable() {

        // given
        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();

        sut.registerForPostFix(masterLine("1"), fattpaLine1);
        sut.registerForPostFix(masterLine("10000"), fattpaLine2);

        sut.appliesRenumbering();

        verify(fattpaLine1).setNumeroLinea(1);
        verify(fattpaLine2).setNumeroLinea(2);
    }

    @Test
    public void aComplexTest() {

        // given
        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();
        FattpaLine fattpaLine3 = fattpaLine();

        sut.registerForPostFix(masterLine("003"), fattpaLine1); // it's a 3! Let's keep it like this.
        sut.registerForPostFix(masterLine("A-1"), fattpaLine2); // it's the first line among the ones with a not numeric Id. Let's give it the first not used number bigger that the max id, 5!
        sut.registerForPostFix(masterLine("004"), fattpaLine3); // it's a 4! Let's keep it like this.
        sut.registerForPostFix(masterLine("10000"), fattpaLine3); // it's out of range, let's treat it like a not numeric Id.

        sut.appliesRenumbering();

        verify(fattpaLine1).setNumeroLinea(3);
        verify(fattpaLine2).setNumeroLinea(5);
        verify(fattpaLine3).setNumeroLinea(4);
        verify(fattpaLine3).setNumeroLinea(6);
    }

    private FattpaLine fattpaLine() {
        FattpaLine mock = mock(FattpaLine.class);
        return mock;
    }

    private CenLine masterLine(String cenLineIdentifier) {
        CenLine mock = mock(CenLine.class);
        when(mock.lineIdentifier()).thenReturn(cenLineIdentifier);
        return mock;
    }

}
