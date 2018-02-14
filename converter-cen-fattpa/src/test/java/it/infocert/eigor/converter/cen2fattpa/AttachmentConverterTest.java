package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import it.infocert.eigor.model.core.model.BT0122SupportingDocumentReference;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AttachmentConverterTest {

    private BG0000Invoice invoice;
    private FatturaElettronicaType fatturaElettronica;

    @Before
    public void setUp() throws Exception {
        invoice = makeInvoiceWithBT122AndNoBT125();
        fatturaElettronica = new FatturaElettronicaType();
        FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
        fatturaElettronica.getFatturaElettronicaBody().add(body);
    }

    @Test
    public void shouldHaveCsvAttachmentIfBT125IsEmpty() throws Exception {
        AttachmentConverter converter = new AttachmentConverter();
        converter.map(invoice,fatturaElettronica,new ArrayList<IConversionIssue>(), ErrorCode.Location.FATTPA_OUT);
        AllegatiType allegati = fatturaElettronica.getFatturaElettronicaBody().get(0).getAllegati().get(0);
        assertThat(allegati.getNomeAttachment(), is("document-reference"));
        assertThat(allegati.getAttachment(), is("Test Reference".getBytes()));
        assertThat(allegati.getFormatoAttachment(), is("csv"));
    }

    private BG0000Invoice makeInvoiceWithBT122AndNoBT125() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();

        BG0024AdditionalSupportingDocuments bg0024AdditionalSupportingDocuments = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122SupportingDocumentReference = new BT0122SupportingDocumentReference("Test Reference");
        bg0024AdditionalSupportingDocuments.getBT0122SupportingDocumentReference().add(bt0122SupportingDocumentReference);

        bg0000Invoice.getBG0024AdditionalSupportingDocuments().add(bg0024AdditionalSupportingDocuments);

        return bg0000Invoice;
    }
}