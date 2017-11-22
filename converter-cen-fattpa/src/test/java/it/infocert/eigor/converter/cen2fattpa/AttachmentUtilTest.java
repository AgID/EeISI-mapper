package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AttachmentUtilTest {

    private final String startingText = "starting: value";
    private AttachmentUtil sut;
    private FatturaElettronicaBodyType fatturaElettronicaBody;


    @Before
    public void setUp() throws Exception {
        sut = new AttachmentUtil();
        fatturaElettronicaBody = new FatturaElettronicaBodyType();
        final AllegatiType allegato = new AllegatiType();
        allegato.setFormatoAttachment("txt");
        allegato.setNomeAttachment("not-mapped-values");
        allegato.setAttachment(startingText.getBytes(StandardCharsets.UTF_8));
        fatturaElettronicaBody.getAllegati().add(allegato);
    }

    @Test
    public void shouldAddToNewAttachment() throws Exception {
        final String input = "additional: text";
        final FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
        sut.addToAttachment(body, input);
        final List<AllegatiType> allegati = body.getAllegati();
        assertFalse(allegati.isEmpty());
        final AllegatiType allegato = allegati.get(0);
        final String attachment = new String(allegato.getAttachment());
        assertEquals(input, attachment);
        System.out.println(attachment);
    }

    @Test
    public void shouldAppendToExistingAttachment() throws Exception {
        final String input = "additional: text";
        sut.addToAttachment(fatturaElettronicaBody, input);
        final AllegatiType allegato = fatturaElettronicaBody.getAllegati().get(0);
        final String attachment = new String(allegato.getAttachment());
        assertEquals(startingText + System.lineSeparator() + input, attachment);
        System.out.println(attachment);
    }
}