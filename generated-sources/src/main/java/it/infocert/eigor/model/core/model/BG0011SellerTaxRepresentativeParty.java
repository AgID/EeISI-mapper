package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.model.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG0011SellerTaxRepresentativeParty implements BTBG {
    private List<BG0012SellerTaxRepresentativePostalAddress> bg0012SellerTaxRepresentativePostalAddresses;
    private List<BT0062SellerTaxRepresentativeName> bt0062SellerTaxRepresentativeNames = new ArrayList<>(0);
    private List<BT0063SellerTaxRepresentativeVatIdentifier> bt0063SellerTaxRepresentativeVatIdentifiers = new ArrayList<>(0);

    public BG0011SellerTaxRepresentativeParty() {
    }

    public List<BG0012SellerTaxRepresentativePostalAddress> getBg0012SellerTaxRepresentativePostalAddresses() {
        return bg0012SellerTaxRepresentativePostalAddresses;
    }

    public void setBg0012SellerTaxRepresentativePostalAddresses(List<BG0012SellerTaxRepresentativePostalAddress> bg0012SellerTaxRepresentativePostalAddresses) {
        this.bg0012SellerTaxRepresentativePostalAddresses = bg0012SellerTaxRepresentativePostalAddresses;
    }

    public List<BT0062SellerTaxRepresentativeName> getBt0062SellerTaxRepresentativeNames() {
        return bt0062SellerTaxRepresentativeNames;
    }

    public void setBt0062SellerTaxRepresentativeNames(List<BT0062SellerTaxRepresentativeName> bt0062SellerTaxRepresentativeNames) {
        this.bt0062SellerTaxRepresentativeNames = bt0062SellerTaxRepresentativeNames;
    }

    public List<BT0063SellerTaxRepresentativeVatIdentifier> getBt0063SellerTaxRepresentativeVatIdentifiers() {
        return bt0063SellerTaxRepresentativeVatIdentifiers;
    }

    public void setBt0063SellerTaxRepresentativeVatIdentifiers(List<BT0063SellerTaxRepresentativeVatIdentifier> bt0063SellerTaxRepresentativeVatIdentifiers) {
        this.bt0063SellerTaxRepresentativeVatIdentifiers = bt0063SellerTaxRepresentativeVatIdentifiers;
    }

    @Override
    public int order() {
        return 73;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg0012SellerTaxRepresentativePostalAddresses);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }
}
