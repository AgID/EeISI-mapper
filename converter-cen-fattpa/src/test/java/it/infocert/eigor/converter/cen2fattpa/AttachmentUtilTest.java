package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import org.junit.Before;
import org.junit.Test;

import javax.activation.MimeType;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
        sut.addToUnmappedValuesAttachment(body, input);
        final List<AllegatiType> allegati = body.getAllegati();
        assertFalse(allegati.isEmpty());
        final AllegatiType allegato = allegati.get(0);
        final String attachment = new String(allegato.getAttachment());
        assertEquals(input, attachment);
    }

    @Test
    public void shouldAppendToExistingAttachment() throws Exception {
        final String input = "additional: text";
        sut.addToUnmappedValuesAttachment(fatturaElettronicaBody, input);
        final AllegatiType allegato = fatturaElettronicaBody.getAllegati().get(0);
        final String attachment = new String(allegato.getAttachment());
        assertEquals(startingText + System.lineSeparator() + input, attachment);
    }

    @Test
    public void shouldMapMimeToShortFileFormatString() throws Exception {
        final MimeType pdfMime = new MimeType("application", "PDF");
        final MimeType xlsxMime = new MimeType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        final MimeType odsMime = new MimeType("application", "vnd.oasis.opendocument.spreadsheet");
        final MimeType pngMime = new MimeType("image", "PNG");
        final MimeType jpegMime = new MimeType("image", "JPEG");
        final MimeType csvMime = new MimeType("text", "CSV");

        assertThat(sut.getShortFileFormat(pdfMime), is("pdf"));
        assertThat(sut.getShortFileFormat(xlsxMime), is("xlsx"));
        assertThat(sut.getShortFileFormat(odsMime), is("ods"));
        assertThat(sut.getShortFileFormat(pngMime), is("png"));
        assertThat(sut.getShortFileFormat(jpegMime), is("jpeg"));
        assertThat(sut.getShortFileFormat(csvMime), is("csv"));
    }
}
