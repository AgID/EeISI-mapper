package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class CoreInvoice {

    private List<BG001InvoiceNote> bg0001InvoiceNotes = new ArrayList<>(0);
    private List<BG002ProcessControl> bg002ProcessControls = new ArrayList<>(0);
    private List<BG011SellerTaxRepresentativeParty> bg11SellerTaxRepresentativeParties = new ArrayList<>(0);

    private List<BT0001InvoiceNumber> bt0001InvoiceNumbers = new ArrayList<>(0);
    private List<BT0006VatAccountingCurrencyCode> bt0006VatAccountingCurrencyCodes = new ArrayList<>(0);

    public List<BG001InvoiceNote> getBg0001InvoiceNotes() {
        return bg0001InvoiceNotes;
    }

    public void setBg0001InvoiceNotes(List<BG001InvoiceNote> bg0001InvoiceNotes) {
        this.bg0001InvoiceNotes = bg0001InvoiceNotes;
    }

    public List<BG002ProcessControl> getBg0002ProcessControls() {
        return bg002ProcessControls;
    }

    public void setBg0002ProcessControls(List<BG002ProcessControl> bg002ProcessControls) {
        this.bg002ProcessControls = bg002ProcessControls;
    }

    public List<BG011SellerTaxRepresentativeParty> getBg11SellerTaxRepresentativeParties() {
        return bg11SellerTaxRepresentativeParties;
    }

    public void setBg11SellerTaxRepresentativeParties(List<BG011SellerTaxRepresentativeParty> bg11SellerTaxRepresentativeParties) {
        this.bg11SellerTaxRepresentativeParties = bg11SellerTaxRepresentativeParties;
    }

    public List<BT0001InvoiceNumber> getBt0001InvoiceNumbers() {
        return bt0001InvoiceNumbers;
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




    public void accept(Visitor v) {
        v.startInvoice(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg0001InvoiceNotes);
        list.addAll(this.bg002ProcessControls);
        list.addAll(this.bg11SellerTaxRepresentativeParties);
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
