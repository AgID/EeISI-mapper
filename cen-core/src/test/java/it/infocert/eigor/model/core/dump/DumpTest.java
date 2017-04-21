package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.enums.*;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class DumpTest {

    @Test
    public void dumpAnInvoice() {

        // given
        BG0000Invoice invoice = new BG0000Invoice();

        // nr. 1

        BT0001InvoiceNumber invoiceNumber = new BT0001InvoiceNumber("2017/01");
        invoice.getBT0001InvoiceNumber().add(invoiceNumber);

        // nr. 5
        BT0006VatAccountingCurrencyCode currencyCode = new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
        invoice.getBT0006VatAccountingCurrencyCode().add(currencyCode);

        // nr. 20
        BG0001InvoiceNote bgInvoiceNote = new BG0001InvoiceNote();

        // nr. 21
        BT0021InvoiceNoteSubjectCode invoiceNoteSubjectCode = new BT0021InvoiceNoteSubjectCode("ABE");
        bgInvoiceNote.getBT0021InvoiceNoteSubjectCode().add(invoiceNoteSubjectCode);

        // nr. 22
        BT0022InvoiceNote btInvoiceNote = new BT0022InvoiceNote("This is a test invoice.");
        bgInvoiceNote.getBT0022InvoiceNote().add(btInvoiceNote);
        invoice.getBG0001InvoiceNote().add(bgInvoiceNote);


        // when
        Visitor v = mock(Visitor.class);
        invoice.accept(v);


        // then
        InOrder inOrder = inOrder(v);
        inOrder.verify(v).startInvoice(invoice);

        inOrder.verify(v).startBTBG(invoiceNumber);
        inOrder.verify(v).endBTBG(invoiceNumber);

        inOrder.verify(v).startBTBG(currencyCode);
        inOrder.verify(v).endBTBG(currencyCode);

        inOrder.verify(v).startBTBG(bgInvoiceNote);

        inOrder.verify(v).startBTBG(invoiceNoteSubjectCode);
        inOrder.verify(v).endBTBG(invoiceNoteSubjectCode);

        inOrder.verify(v).startBTBG(btInvoiceNote);
        inOrder.verify(v).endBTBG(btInvoiceNote);

        inOrder.verify(v).endBTBG(bgInvoiceNote);

        inOrder.verify(v).endInvoice(invoice);

    }



}
