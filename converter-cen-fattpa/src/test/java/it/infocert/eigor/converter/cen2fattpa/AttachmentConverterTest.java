package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.fattpa.commons.models.AllegatiType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaBodyType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0006SellerContact;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BG0009BuyerContact;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import it.infocert.eigor.model.core.model.BT0020PaymentTerms;
import it.infocert.eigor.model.core.model.BT0041SellerContactPoint;
import it.infocert.eigor.model.core.model.BT0056BuyerContactPoint;
import it.infocert.eigor.model.core.model.BT0122SupportingDocumentReference;
import it.infocert.eigor.model.core.model.BT0123SupportingDocumentDescription;
import it.infocert.eigor.model.core.model.BT0124ExternalDocumentLocation;
import it.infocert.eigor.model.core.model.BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
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
        converter.map(invoice, fatturaElettronica, new ArrayList<>(), ErrorCode.Location.FATTPA_OUT, null);
        AllegatiType allegati = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegati.getNomeAttachment(), is("document-reference"));
        assertThat(allegati.getAttachment(), is("Test Reference".getBytes()));
        assertThat(allegati.getFormatoAttachment(), is("csv"));
    }

    @Test
    public void shouldHaveJustOneAllegatiForAnAttachedDocument() {

        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT122AndBT125();
        converter.map(invoice, fatturaElettronica, new ArrayList<>(), ErrorCode.Location.FATTPA_OUT, null);
        AllegatiType allegati = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegati.getNomeAttachment(), is("Test Reference-file.txt"));
        assertThat(allegati.getDescrizioneAttachment(), is("description"));
        assertThat(allegati.getAttachment(), is("TESTCONTENT".getBytes()));
        assertThat(allegati.getFormatoAttachment(), is("csv"));
    }

    @Test
    public void shouldHaveTwoAllegatiIfBothExternalReferenceAndAttachedDocument() {
        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT122AndBT124AndBT125();
        converter.map(invoice, fatturaElettronica, new ArrayList<>(), ErrorCode.Location.FATTPA_OUT, null);

        // one for the external reference and one for the attached document

        AllegatiType allegatiAttached = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegatiAttached.getFormatoAttachment(), is("csv"));
        assertThat(allegatiAttached.getAttachment(), is("external".getBytes()));

        AllegatiType allegatiReference = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(1);
        assertThat(allegatiReference.getNomeAttachment(), is("Test Reference-file.txt"));
        assertThat(allegatiReference.getAttachment(), is("TESTCONTENT".getBytes()));
    }

    @Test
    public void shouldPutInAllegatiBt20AndBt41AndBt56() {
        AttachmentConverter converter = new AttachmentConverter();
        BG0000Invoice invoice = makeInvoiceWithBT20AndBt41AndBt56();
        converter.map(invoice, fatturaElettronica, new ArrayList<>(), ErrorCode.Location.FATTPA_OUT, null);

        // one for the external reference and one for the attached document

        AllegatiType allegatiAttached = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegatiAttached.getFormatoAttachment(), is("txt"));
        final String[] bts = new String(allegatiAttached.getAttachment()).split(System.lineSeparator());
        assertEquals(3, bts.length);
        assertEquals("BT0020: BT20", bts[0]);
        assertEquals("BT0041: BT41", bts[1]);
        assertEquals("BT0056: BT56", bts[2]);
    }

    private BG0000Invoice makeInvoiceWithBT122AndNoBT125() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BG0024AdditionalSupportingDocuments bg0024AdditionalSupportingDocuments = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122SupportingDocumentReference = new BT0122SupportingDocumentReference("Test Reference");
        bg0024AdditionalSupportingDocuments.getBT0122SupportingDocumentReference().add(bt0122SupportingDocumentReference);

        bg0000Invoice.getBG0024AdditionalSupportingDocuments().add(bg0024AdditionalSupportingDocuments);

        return bg0000Invoice;
    }

    private BG0000Invoice makeInvoiceWithBT20AndBt41AndBt56() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BT0020PaymentTerms bt20 = new BT0020PaymentTerms("BT20");
        bg0000Invoice.getBT0020PaymentTerms().add(bt20);

        BG0004Seller bg4 = new BG0004Seller();
        BG0006SellerContact bg6 = new BG0006SellerContact();
        BT0041SellerContactPoint bt41 = new BT0041SellerContactPoint("BT41");
        bg6.getBT0041SellerContactPoint().add(bt41);
        bg4.getBG0006SellerContact().add(bg6);
        bg0000Invoice.getBG0004Seller().add(bg4);

        BG0007Buyer bg7 = new BG0007Buyer();
        BG0009BuyerContact bg9 = new BG0009BuyerContact();
        BT0056BuyerContactPoint bt56 = new BT0056BuyerContactPoint("BT56");
        bg9.getBT0056BuyerContactPoint().add(bt56);
        bg7.getBG0009BuyerContact().add(bg9);
        bg0000Invoice.getBG0007Buyer().add(bg7);

        return bg0000Invoice;
    }

    private BG0000Invoice makeInvoiceWithBT122AndBT125() {
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
