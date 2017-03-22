package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DumpTest {

    @Test
    public void dumpAnInvoice() {

        // given
        CoreInvoice invoice = new CoreInvoice();

        // nr. 1
        BT0001InvoiceNumber invoiceNumber = new BT0001InvoiceNumber("2017/01");
        invoice.getBT0001InvoiceNumbers().add(invoiceNumber);

        // nr. 5
        BT0006VatAccountingCurrencyCode currencyCode = new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
        invoice.getBt0006VatAccountingCurrencyCodes().add(currencyCode);

        // nr. 20
        BG001InvoiceNote bgInvoiceNote = new BG001InvoiceNote();

        // nr. 21
        BT0021InvoiceNoteSubjectCode invoiceNoteSubjectCode = new BT0021InvoiceNoteSubjectCode("ABE");
        bgInvoiceNote.getBt0021InvoiceNoteSubjectCodes().add(invoiceNoteSubjectCode);

        // nr. 22
        BT0022InvoiceNote btInvoiceNote = new BT0022InvoiceNote("This is a test invoice.");
        bgInvoiceNote.getBt0022InvoiceNotes().add(btInvoiceNote);
        invoice.getBg001InvoiceNotes().add(bgInvoiceNote);


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
