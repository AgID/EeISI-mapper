package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.dump.Visitor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG11SellerTaxRepresentativeParty implements BTBG {
    private List<BG12SellerTaxRepresentativePostalAddress> bg12SellerTaxRepresentativePostalAddresses;

    public BG11SellerTaxRepresentativeParty() {
    }

    public List<BG12SellerTaxRepresentativePostalAddress> getBg12SellerTaxRepresentativePostalAddresses() {
        return bg12SellerTaxRepresentativePostalAddresses;
    }

    public void setBg12SellerTaxRepresentativePostalAddresses(List<BG12SellerTaxRepresentativePostalAddress> bg12SellerTaxRepresentativePostalAddresses) {
        this.bg12SellerTaxRepresentativePostalAddresses = bg12SellerTaxRepresentativePostalAddresses;
    }

    @Override
    public int order() {
        return 73;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bg12SellerTaxRepresentativePostalAddresses);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }
}
