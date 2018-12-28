package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.LinePostFixSupport.CenLine;
import it.infocert.eigor.converter.cen2fattpa.LinePostFixSupport.FattpaLine;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class LinePostFixSupportTest {

    @Test
    public void shouldReplaceANotNumberWithANumberBiggerThanTheMaximumNumberUsed() {

        // given
        LinePostFixSupport sut = new LinePostFixSupport();

        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();
        FattpaLine fattpaLine3 = fattpaLine();

        sut.registerForPostFix(masterLine("003"), fattpaLine1);
        sut.registerForPostFix(masterLine("A-1"), fattpaLine2);
        sut.registerForPostFix(masterLine("004"), fattpaLine3);

        sut.postfix();

        verify(fattpaLine1).setNumeroLinea(3);
        verify(fattpaLine2).setNumeroLinea(5);
        verify(fattpaLine3).setNumeroLinea(4);
    }

    @Test
    public void shouldReplace10000WithTheFirstNumberAvailable() {

        // given
        LinePostFixSupport sut = new LinePostFixSupport();

        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();

        sut.registerForPostFix(masterLine("1"), fattpaLine1);
        sut.registerForPostFix(masterLine("10000"), fattpaLine2);

        sut.postfix();

        verify(fattpaLine1).setNumeroLinea(1);
        verify(fattpaLine2).setNumeroLinea(2);
    }

    @Test
    public void aComplexTest() {

        // given
        LinePostFixSupport sut = new LinePostFixSupport();

        FattpaLine fattpaLine1 = fattpaLine();
        FattpaLine fattpaLine2 = fattpaLine();
        FattpaLine fattpaLine3 = fattpaLine();
        FattpaLine fattpaLine4 = fattpaLine();

        sut.registerForPostFix(masterLine("003"), fattpaLine1);
        sut.registerForPostFix(masterLine("A-1"), fattpaLine2);
        sut.registerForPostFix(masterLine("004"), fattpaLine3);
        sut.registerForPostFix(masterLine("10000"), fattpaLine3);

        sut.postfix();

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
