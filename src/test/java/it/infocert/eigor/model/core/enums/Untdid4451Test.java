package it.infocert.eigor.model.core.enums;

import it.infocert.eigor.model.core.enums.Untdid4451;
import org.junit.Test;

import static org.junit.Assert.*;

public class Untdid4451Test {

    @Test
    public void toDetailedString() throws Exception {
        assertEquals("+ADY|Container stripping instructions|Instructions regarding the stripping of container(s).", Untdid4451.ADY.toDetailedString() );
        assertEquals("AAK|Price conditions|Information on the price conditions that are expected or given.", Untdid4451.AAK.toDetailedString() );
        assertEquals("AAB|Terms of payments|[4276] Conditions of payment between the parties to a transaction (generic term).", Untdid4451.AAB.toDetailedString() );
    }

}