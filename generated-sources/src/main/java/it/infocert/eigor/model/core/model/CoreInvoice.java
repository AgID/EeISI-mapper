package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class CoreInvoice {

    private List<BG0001InvoiceNote> bg0001InvoiceNotes = new ArrayList<>(0);
    private List<BG0002ProcessControl> bg0002ProcessControls = new ArrayList<>(0);
    private List<BG0011SellerTaxRepresentativeParty> bg0011SellerTaxRepresentativeParties = new ArrayList<>(0);
    private List<BG0004Sellers> bg0004Sellers = new ArrayList<>(0);

    private List<BT0001InvoiceNumber> bt0001InvoiceNumbers = new ArrayList<>(0);
    private List<BT0006VatAccountingCurrencyCode> bt0006VatAccountingCurrencyCodes = new ArrayList<>(0);

    public List<BG0001InvoiceNote> getBg0001InvoiceNotes() {
        return bg0001InvoiceNotes;
    }

    public void setBg0001InvoiceNotes(List<BG0001InvoiceNote> bg0001InvoiceNotes) {
        this.bg0001InvoiceNotes = bg0001InvoiceNotes;
    }

    public List<BG0002ProcessControl> getBg0002ProcessControls() {
        return bg0002ProcessControls;
    }

    public void setBg0002ProcessControls(List<BG0002ProcessControl> bg0002ProcessControls) {
        this.bg0002ProcessControls = bg0002ProcessControls;
    }

    public List<BG0011SellerTaxRepresentativeParty> getBg0011SellerTaxRepresentativeParties() {
        return bg0011SellerTaxRepresentativeParties;
    }


    public void setBt0001InvoiceNumbers(List<BT0001InvoiceNumber> bt0001InvoiceNumbers) {
        this.bt0001InvoiceNumbers = bt0001InvoiceNumbers;
    }

    public List<BT0006VatAccountingCurrencyCode> getBt0006VatAccountingCurrencyCodes() {
        return bt0006VatAccountingCurrencyCodes;
    }

    public void setBt0006VatAccountingCurrencyCodes(List<BT0006VatAccountingCurrencyCode> bt0006VatAccountingCurrencyCodes) {
        this.bt0006VatAccountingCurrencyCodes = bt0006VatAccountingCurrencyCodes;
    }


    public void setBg0011SellerTaxRepresentativeParties(List<BG0011SellerTaxRepresentativeParty> bg0011SellerTaxRepresentativeParties) {
        this.bg0011SellerTaxRepresentativeParties = bg0011SellerTaxRepresentativeParties;
    }

    public List<BT0001InvoiceNumber> getBt0001InvoiceNumbers() {
        return bt0001InvoiceNumbers;
    }

    public List<BG0004Sellers> getBg0004Sellers() {
        return bg0004Sellers;
    }

    public void setBg0004Sellers(List<BG0004Sellers> bg0004Sellers) {
        this.bg0004Sellers = bg0004Sellers;
    }

    public void accept(Visitor v) {
        v.startInvoice(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg0001InvoiceNotes);
        list.addAll(this.bg0002ProcessControls);
        list.addAll(this.bg0011SellerTaxRepresentativeParties);
        list.addAll(this.bt0001InvoiceNumbers);
        list.addAll(this.bt0006VatAccountingCurrencyCodes);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endInvoice(this);
    }

    public List getBT0001InvoiceNumbers() {
        return this.bt0001InvoiceNumbers;
    }
}
