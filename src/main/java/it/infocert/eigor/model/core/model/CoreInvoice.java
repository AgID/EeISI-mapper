package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class CoreInvoice {

    private List<BG01InvoiceNote> bg01InvoiceNotes = new ArrayList<>(0);
    private List<BG02ProcessControl> bg02ProcessControls = new ArrayList<>(0);
    private List<BG11SellerTaxRepresentativeParty> bg11SellerTaxRepresentativeParties = new ArrayList<>(0);

    private List<BT01InvoiceNumber> bt01InvoiceNumbers = new ArrayList<>(0);
    private List<BT006VatAccountingCurrencyCode> bt006VatAccountingCurrencyCodes = new ArrayList<>(0);

    public List<BG01InvoiceNote> getBg01InvoiceNotes() {
        return bg01InvoiceNotes;
    }

    public void setBg01InvoiceNotes(List<BG01InvoiceNote> bg01InvoiceNotes) {
        this.bg01InvoiceNotes = bg01InvoiceNotes;
    }

    public List<BG02ProcessControl> getBg02ProcessControls() {
        return bg02ProcessControls;
    }

    public void setBg02ProcessControls(List<BG02ProcessControl> bg02ProcessControls) {
        this.bg02ProcessControls = bg02ProcessControls;
    }

    public List<BG11SellerTaxRepresentativeParty> getBg11SellerTaxRepresentativeParties() {
        return bg11SellerTaxRepresentativeParties;
    }

    public void setBg11SellerTaxRepresentativeParties(List<BG11SellerTaxRepresentativeParty> bg11SellerTaxRepresentativeParties) {
        this.bg11SellerTaxRepresentativeParties = bg11SellerTaxRepresentativeParties;
    }

    public List<BT01InvoiceNumber> getBt01InvoiceNumbers() {
        return bt01InvoiceNumbers;
    }

    public void setBt01InvoiceNumbers(List<BT01InvoiceNumber> bt01InvoiceNumbers) {
        this.bt01InvoiceNumbers = bt01InvoiceNumbers;
    }

    public List<BT006VatAccountingCurrencyCode> getBt006VatAccountingCurrencyCodes() {
        return bt006VatAccountingCurrencyCodes;
    }

    public void setBt006VatAccountingCurrencyCodes(List<BT006VatAccountingCurrencyCode> bt006VatAccountingCurrencyCodes) {
        this.bt006VatAccountingCurrencyCodes = bt006VatAccountingCurrencyCodes;
    }


    public void accept(Visitor v) {
        v.startInvoice(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg01InvoiceNotes);
        list.addAll(this.bg02ProcessControls);
        list.addAll(this.bg11SellerTaxRepresentativeParties);
        list.addAll(this.bt01InvoiceNumbers);
        list.addAll(this.bt006VatAccountingCurrencyCodes);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endInvoice(this);
    }
}
