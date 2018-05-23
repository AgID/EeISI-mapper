package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.*;

import java.util.Arrays;

public class DumpVisitor implements Visitor {

    private final StringBuilder sb = new StringBuilder();
    private boolean lineAlreadyPrinted = false;
    private int nestingLevel;

    @Override
    public void startInvoice(BG0000Invoice invoice) {
        sb.append("========================================\n");
        sb.append("INVOICE                                 \n");
        sb.append("========================================\n");
        nestingLevel = 0;
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

        sb.append(String.format("%s%3s | %6s | %-10s | %s\n", nestingSpacer(), order, name, value, btbg.name()));

        if(btbg.denomination().startsWith("BG")){
            nestingLevel++;
        }
    }

    @Override
    public void endBTBG(BTBG btbg) {
        if(btbg.denomination().startsWith("BG")){
            sb.append("----------------------------------------\n");
            nestingLevel--;
            sb.append(String.format("%s%3s | %6s | %-10s \n", nestingSpacer(), "---", btbg.denomination() + " ended", ""));
        }
    }

    @Override
    public void endInvoice(BG0000Invoice invoice) {
        sb.append("========================================\n");
    }

    @Override
    public String toString() {
        return sb.toString();
    }


    public static void main(String[] args) {

        // JUST A DEMO

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
        BT0021InvoiceNoteSubjectCode invoiceNoteSubjectCode = new BT0021InvoiceNoteSubjectCode(Untdid4451InvoiceNoteSubjectCode.AAA.toDetailedString());
        bgInvoiceNote.getBT0021InvoiceNoteSubjectCode().add(invoiceNoteSubjectCode);

        // nr. 22
        BT0022InvoiceNote btInvoiceNote = new BT0022InvoiceNote("This is a test invoice.");
        bgInvoiceNote.getBT0022InvoiceNote().add(btInvoiceNote);
        invoice.getBG0001InvoiceNote().add(bgInvoiceNote);

        DumpVisitor v = new DumpVisitor();
        invoice.accept(v);
        System.out.println(v.toString());
    }

    private String nestingSpacer() {
        byte[] nesting = new byte[nestingLevel * 3];
        Arrays.fill(nesting, " ".getBytes()[0]);
        return new String(nesting);
    }
}
