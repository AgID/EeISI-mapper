package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG012SellerTaxRepresentativePostalAddress implements BTBG {
    private List<BT064TaxRepresentativeAddressLine1> bt64TaxRepresentativeAddressLines = new ArrayList<>(0);;

    public BG012SellerTaxRepresentativePostalAddress() {
    }

    public List<BT064TaxRepresentativeAddressLine1> getBt64TaxRepresentativeAddressLines() {
        return bt64TaxRepresentativeAddressLines;
    }

    public void setBt64TaxRepresentativeAddressLines(List<BT064TaxRepresentativeAddressLine1> bt64TaxRepresentativeAddressLines) {
        this.bt64TaxRepresentativeAddressLines = bt64TaxRepresentativeAddressLines;
    }

    @Override
    public int order() {
        return 76;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bt64TaxRepresentativeAddressLines);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }
}
