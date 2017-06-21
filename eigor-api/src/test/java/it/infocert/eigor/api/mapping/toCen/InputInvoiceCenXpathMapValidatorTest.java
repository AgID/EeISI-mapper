package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class InputInvoiceCenXpathMapValidatorTest {

    private InputInvoiceCenXpathMapValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new InputInvoiceCenXpathMapValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?");
    }

    @Test
    public void shouldPassIfKeysMatchRegexAndXpathsAreValid() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "/Invoice/BillingReference/InvoiceDocumentReference/ID");
        mappings.put("/BT0125", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject");
        mappings.put("/BT0125-1", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject/@mimeCode");

        validator.validate(mappings);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfAKeyValueDoesNotMatchRegex() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "/Invoice/ProfileID");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "/Invoice/BillingReference/InvoiceDocumentReference/ID");
        mappings.put("/BG0010", "/Invoice/PayeeParty");
        mappings.put("/BT0125", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject");
        mappings.put("/BT0125-1", "/Invoice/AdditionalDocumentReference/Attachment/EmbeddedDocumentBinaryObject/@mimeCode");

        mappings.put("/BT019", "/Invoice/AccountingCost");

        validator.validate(mappings);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfAMappingValueIsEmpty() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "/Invoice/ProfileID");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");
        mappings.put("/BT0025", "");

        validator.validate(mappings);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfAMappingValueIsNotAValidXpath() throws Exception {
        Multimap<String, String> mappings = HashMultimap.create();
        mappings.put("/BT0022", "\\NOTXPATH");
        mappings.put("/BT0023", "/Invoice/CustomizationID");
        mappings.put("/BT0024", "/Invoice/Note");

        validator.validate(mappings);
    }

}