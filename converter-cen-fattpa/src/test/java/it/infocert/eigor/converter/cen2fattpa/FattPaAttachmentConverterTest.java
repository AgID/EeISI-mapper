package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0010Payee;
import it.infocert.eigor.model.core.model.BT0060PayeeIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FattPaAttachmentConverterTest {

    private List<String> bts;
    private Reflections reflections;
    private ArrayList<IConversionIssue> errors;
    private ConversionRegistry conversionRegistry;

    @Before
    public void setUp() throws Exception {
        bts = new ArrayList<>(2);
        bts.add("/BG0010/BT0060");
        bts.add("/BG0010/BT0061");

        errors = new ArrayList<>(0);
        reflections = new Reflections("it.infocert.eigor");

        conversionRegistry = new ConversionRegistry();
    }

    @Test
    public void shouldCreateAConverterFromAListOfBtBgs() throws Exception {
        BG0000Invoice invoice = new BG0000Invoice();
        FattPaAttachmentConverter.builder(conversionRegistry, reflections, invoice, errors).path("/BG0010/BT0060").path("/BG0010/BT0061").build();
        FattPaAttachmentConverter.builder(conversionRegistry, reflections, invoice, errors).pathsList(bts);
    }

    @Test
    public void shouldReturnAStringThatContainsTheProvidedFields() throws Exception {
        FattPaAttachmentConverter attachmentConverter = createConverter();
        String attachment = attachmentConverter.createAttachment();

        assertNotNull(attachment);
        assertTrue(errors.isEmpty());

        for (String bt : bts) {
            bt = bt.split("/")[2];
            assertTrue(attachment.contains(bt));
        }
    }

    @Test
    public void shouldReturnAStringProperlyFormatted() throws Exception {
        String regex = "(BT\\d{4}: .*(\\r)*\n)*";

        FattPaAttachmentConverter converter = createConverter();
        String attachment = converter.createAttachment();

        assertNotNull(attachment);
        assertTrue(attachment.matches(regex));
    }

//    @Test
    public void name() throws Exception {
        BG0000Invoice invoice = createInvoice();
        BG0010Payee payee = new BG0010Payee();

        payee.getBT0060PayeeIdentifierAndSchemeIdentifier().add(new BT0060PayeeIdentifierAndSchemeIdentifier("Id"));
        payee.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier().add(new BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier("Id"));
        invoice.getBG0010Payee().add(payee);

        FattPaAttachmentConverter attachmentConverter = FattPaAttachmentConverter.builder(conversionRegistry, reflections, invoice, errors).pathsList(bts).build();
        byte[] bytes = attachmentConverter.createAttachment().getBytes();

        FileOutputStream fos = new FileOutputStream("C:\\Users\\Matteo\\Documenti\\attachment.txt");
        fos.write(bytes);
        fos.close();
    }

    private FattPaAttachmentConverter createConverter() {
        FattPaAttachmentConverter.Builder builder = FattPaAttachmentConverter.builder(conversionRegistry, reflections, createInvoice(), errors).pathsList(bts);
        return builder.build();
    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0010Payee payee = new BG0010Payee();

        payee.getBT0060PayeeIdentifierAndSchemeIdentifier().add(new BT0060PayeeIdentifierAndSchemeIdentifier("Id"));
        payee.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier().add(new BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier("Id"));

        invoice.getBG0010Payee().add(payee);

        return invoice;
    }
}
