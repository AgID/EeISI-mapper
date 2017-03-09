package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG011SellerTaxRepresentativeParty implements BTBG {
    private List<BG012SellerTaxRepresentativePostalAddress> bg012SellerTaxRepresentativePostalAddresses;

    public BG011SellerTaxRepresentativeParty() {
    }

    public List<BG012SellerTaxRepresentativePostalAddress> getBg012SellerTaxRepresentativePostalAddresses() {
        return bg012SellerTaxRepresentativePostalAddresses;
    }

    public void setBg012SellerTaxRepresentativePostalAddresses(List<BG012SellerTaxRepresentativePostalAddress> bg012SellerTaxRepresentativePostalAddresses) {
        this.bg012SellerTaxRepresentativePostalAddresses = bg012SellerTaxRepresentativePostalAddresses;
    }

    @Override
    public int order() {
        return 73;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg012SellerTaxRepresentativePostalAddresses);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }
}
