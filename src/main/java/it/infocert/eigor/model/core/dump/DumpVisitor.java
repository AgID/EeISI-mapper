package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.*;

public class DumpVisitor implements Visitor {

    private final StringBuilder sb = new StringBuilder();
    private boolean lineAlreadyPrinted = false;

    @Override
    public void startInvoice(CoreInvoice invoice) {
        sb.append("========================================\n");
        sb.append("INVOICE                                 \n");
        sb.append("========================================\n");
    }

    @Override
    public void startBTBG(BTBG btbg) {

        String order = String.valueOf(btbg.order());
        String name = String.valueOf(btbg.denomination());
        String value = btbg.toString();

        if(!lineAlreadyPrinted){
            lineAlreadyPrinted = true;
        }else{
            sb.append("----------------------------------------\n");
        }
        sb.append(String.format("%3s |%6s | %-10s \n", order, name, value));
    }

    @Override
    public void endBTBG(BTBG btbg) {

    }

    @Override
    public void endInvoice(CoreInvoice invoice) {
        sb.append("========================================\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }


    public static void main(String[] args) {

        // JUST A DEMO

        // given
        CoreInvoice invoice = new CoreInvoice();

        // nr. 1
        BT001InvoiceNumber invoiceNumber = new BT001InvoiceNumber(new Identifier("2017/01"));
        invoice.getBt001InvoiceNumbers().add(invoiceNumber);

        // nr. 5
        BT006VatAccountingCurrencyCode currencyCode = new BT006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
        invoice.getBt006VatAccountingCurrencyCodes().add(currencyCode);

        // nr. 20
        BG001InvoiceNote bgInvoiceNote = new BG001InvoiceNote();

        // nr. 21
        BT021InvoiceNoteSubjectCode invoiceNoteSubjectCode = new BT021InvoiceNoteSubjectCode(Untdid4451InvoiceNoteSubjectCode.AAA);
        bgInvoiceNote.getBt021InvoiceNoteSubjectCodes().add(invoiceNoteSubjectCode);

        // nr. 22
        BT022InvoiceNote btInvoiceNote = new BT022InvoiceNote("This is a test invoice.");
        bgInvoiceNote.getBt022InvoiceNotes().add(btInvoiceNote);
        invoice.getBg001InvoiceNotes().add(bgInvoiceNote);

        DumpVisitor v = new DumpVisitor();
        invoice.accept(v);
        System.out.println(v.toString());
    }
}
