package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso4217CurrencyCode;
import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCode;
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
        BT01InvoiceNumber invoiceNumber = new BT01InvoiceNumber(new Identifier("2017/01"));
        invoice.getBt01InvoiceNumbers().add(invoiceNumber);

        // nr. 5
        BT006VatAccountingCurrencyCode currencyCode = new BT006VatAccountingCurrencyCode(Iso4217CurrencyCode.EUR);
        invoice.getBt006VatAccountingCurrencyCodes().add(currencyCode);

        // nr. 20
        BG01InvoiceNote bgInvoiceNote = new BG01InvoiceNote();

        // nr. 21
        BT21InvoiceNoteSubjectCode invoiceNoteSubjectCode = new BT21InvoiceNoteSubjectCode(Untdid4451InvoiceNoteSubjectCode.ABE);
        bgInvoiceNote.getBt21InvoiceNoteSubjectCodes().add(invoiceNoteSubjectCode);

        // nr. 22
        BT22InvoiceNote btInvoiceNote = new BT22InvoiceNote("This is a test invoice.");
        bgInvoiceNote.getBt22InvoiceNotes().add(btInvoiceNote);
        invoice.getBg01InvoiceNotes().add(bgInvoiceNote);


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
