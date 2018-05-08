package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AttachmentConverterTest {

    private FatturaElettronicaType fatturaElettronica;

    @Before
    public void setUp() {
        fatturaElettronica = new FatturaElettronicaType();
        FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
        fatturaElettronica.getFatturaElettronicaBody().add(body);
    }

    @Test
    public void shouldHaveCsvAttachmentIfBT125IsEmpty() {
        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT122AndNoBT125();
        converter.map(invoice, fatturaElettronica, new ArrayList<IConversionIssue>(), ErrorCode.Location.FATTPA_OUT);
        AllegatiType allegati = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegati.getNomeAttachment(), is("document-reference"));
        assertThat(allegati.getAttachment(), is("Test Reference".getBytes()));
        assertThat(allegati.getFormatoAttachment(), is("csv"));
    }

    @Test
    public void shouldHaveJustOneAlegattiForAnAttachedDocument() throws Exception {

        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT122AndBT125();
        converter.map(invoice, fatturaElettronica, new ArrayList<IConversionIssue>(), ErrorCode.Location.FATTPA_OUT);
        AllegatiType allegati = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegati.getNomeAttachment(), is("Test Reference-file.txt"));
        assertThat(allegati.getDescrizioneAttachment(), is("description"));
        assertThat(allegati.getAttachment(), is("TESTCONTENT".getBytes()));
        assertThat(allegati.getFormatoAttachment(), is("csv"));
    }

    @Test
    public void shouldHaveTwoAlegattiIfBothExternalReferenceAndAttachedDocument() {
        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT122AndBT124AndBT125();
        converter.map(invoice, fatturaElettronica, new ArrayList<IConversionIssue>(), ErrorCode.Location.FATTPA_OUT);

        // one for the external reference and one for the attached document

        AllegatiType allegatiAttached = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegatiAttached.getFormatoAttachment(), is("csv"));
        assertThat(allegatiAttached.getAttachment(), is("external".getBytes()));

        AllegatiType allegatiReference = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(1);
        assertThat(allegatiReference.getNomeAttachment(), is("Test Reference-file.txt"));
        assertThat(allegatiReference.getAttachment(), is("TESTCONTENT".getBytes()));
    }

    private BG0000Invoice makeInvoiceWithBT122AndNoBT125() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BG0024AdditionalSupportingDocuments bg0024AdditionalSupportingDocuments = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122SupportingDocumentReference = new BT0122SupportingDocumentReference("Test Reference");
        bg0024AdditionalSupportingDocuments.getBT0122SupportingDocumentReference().add(bt0122SupportingDocumentReference);

        bg0000Invoice.getBG0024AdditionalSupportingDocuments().add(bg0024AdditionalSupportingDocuments);

        return bg0000Invoice;
    }

    private BG0000Invoice makeInvoiceWithBT122AndBT125() throws Exception {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BG0024AdditionalSupportingDocuments bg0024AdditionalSupportingDocuments = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122SupportingDocumentReference = new BT0122SupportingDocumentReference("Test Reference");
        bg0024AdditionalSupportingDocuments.getBT0122SupportingDocumentReference().add(bt0122SupportingDocumentReference);

        BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription("description");
        bg0024AdditionalSupportingDocuments.getBT0123SupportingDocumentDescription().add(bt0123);

        TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.FATTPA_OUT);
        Element fakeContent = new Element("FakeContent");
        fakeContent.setAttribute("mimeCode", "text/csv");
        fakeContent.setAttribute("filename", "file.txt");
        fakeContent.setText("TESTCONTENT");
        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125;
        try {
            bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(fakeContent));
        } catch (ConversionFailedException e) {
            throw new RuntimeException(e);
        }
        bg0024AdditionalSupportingDocuments.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);

        bg0000Invoice.getBG0024AdditionalSupportingDocuments().add(bg0024AdditionalSupportingDocuments);

        return bg0000Invoice;
    }

    private BG0000Invoice makeInvoiceWithBT122AndBT124AndBT125() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BG0024AdditionalSupportingDocuments bg0024AdditionalSupportingDocuments = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122SupportingDocumentReference = new BT0122SupportingDocumentReference("Test Reference");
        bg0024AdditionalSupportingDocuments.getBT0122SupportingDocumentReference().add(bt0122SupportingDocumentReference);

        BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription("description");
        bg0024AdditionalSupportingDocuments.getBT0123SupportingDocumentDescription().add(bt0123);

        BT0124ExternalDocumentLocation bt0124 = new BT0124ExternalDocumentLocation("external");
        bg0024AdditionalSupportingDocuments.getBT0124ExternalDocumentLocation().add(bt0124);

        TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.CII_OUT);
        Element fakeContent = new Element("FakeContent");
        fakeContent.setAttribute("mimeCode", "text/csv");
        fakeContent.setAttribute("filename", "file.txt");
        fakeContent.setText("TESTCONTENT");
        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125;
        try {
            bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(fakeContent));
        } catch (ConversionFailedException e) {
            throw new RuntimeException(e);
        }
        bg0024AdditionalSupportingDocuments.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);

        bg0000Invoice.getBG0024AdditionalSupportingDocuments().add(bg0024AdditionalSupportingDocuments);

        return bg0000Invoice;
    }
}