package it.infocert.eigor.model.core.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Untdid4451InvoiceNoteSubjectCodeTest {

    @Test
    public void toDetailedString() throws Exception {
        assertEquals("+ADY|Container stripping instructions|Instructions regarding the stripping of container(s).", Untdid4451InvoiceNoteSubjectCode.ADY.toDetailedString() );
        assertEquals("AAK|Price conditions|Information on the price conditions that are expected or given.", Untdid4451InvoiceNoteSubjectCode.AAK.toDetailedString() );
        assertEquals("AAB|Terms of payments|[4276] Conditions of payment between the parties to a transaction (generic term).", Untdid4451InvoiceNoteSubjectCode.AAB.toDetailedString() );
    }

}