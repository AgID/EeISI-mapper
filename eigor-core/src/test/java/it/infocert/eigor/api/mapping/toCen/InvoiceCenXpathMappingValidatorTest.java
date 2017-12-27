package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.Before;
import org.junit.Test;


public class InvoiceCenXpathMappingValidatorTest {

    private InvoiceCenXpathMappingValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new InvoiceCenXpathMappingValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?", new JavaReflections());
    }

    @Test
    public void shouldPassIfKeysMatchRegexAndXpathsAreValid() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "/Invoice/BillingReference/InvoiceDocumentReference/ID");
        mappings.put("/BT0125", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject");
//        mappings.put("/BT0125-1", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject/@mimeCode");
//        TODO uncomment once identifiers are supported in BGs/BTs
        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAKeyValueDoesNotMatchRegex() throws Exception {

        // Why expecting a RuntimeException ?
        // What is the line that is supposed to raise that exception ?
        // How can a developer guess what changed if this tests stops passing ?

        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "/Invoice/ProfileID");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "/Invoice/BillingReference/InvoiceDocumentReference/ID");
        mappings.put("/BG0010", "/Invoice/PayeeParty");
        mappings.put("/BT0125", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject");
        mappings.put("/BT0125-1", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject/@mimeCode");
//        TODO uncomment once identifiers are supported in BGs/BTs
        mappings.put("/BT019", "/Invoice/AccountingCost");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAKeyValueDoesNotExistInCen() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();

        mappings.put("/BT9999", "/Invoice/Note");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "/Invoice/ProfileID");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "");

        validator.validate(mappings);
    }

    @Test(expected = SyntaxErrorInMappingFileException.class)
    public void shouldThrowExceptionIfAMappingValueIsNotAValidXpath() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "\\NOTXPATH");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");

        validator.validate(mappings);
    }

}