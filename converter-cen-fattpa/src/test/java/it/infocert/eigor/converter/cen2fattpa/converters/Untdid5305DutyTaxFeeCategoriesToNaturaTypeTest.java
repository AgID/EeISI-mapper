package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.fattpa.commons.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static it.infocert.eigor.fattpa.commons.models.NaturaType.*;
import static it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Untdid5305DutyTaxFeeCategoriesToNaturaTypeTest {

    private Untdid5305DutyTaxFeeCategories input;
    private NaturaType expectedOutput;

    Untdid5305DutyTaxFeeCategoriesToNaturaType sut = new Untdid5305DutyTaxFeeCategoriesToNaturaType();

    @Parameterized.Parameters
    public static List<Object> untdidAndNatura() {
        return Arrays.asList(new Object[][] {
                { A, null },
                { AA, null },
                { AB, null },
                { AC, null },
                { AD, null },
                { B, null },
                { C, null },
                { E, N_4 },
                { G, N_3 },
                { H, null },
                { O, N_2 },
                { S, null },
                { Z, null },

        });
    }

    public Untdid5305DutyTaxFeeCategoriesToNaturaTypeTest(Untdid5305DutyTaxFeeCategories input, NaturaType expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void convert() {
        assertEquals(expectedOutput, sut.convert(input));
    }
}
