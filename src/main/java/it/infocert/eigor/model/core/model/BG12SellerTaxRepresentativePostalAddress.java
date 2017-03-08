package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

public class BG12SellerTaxRepresentativePostalAddress {
    private List<BT64TaxRepresentativeAddressLine1> bt64TaxRepresentativeAddressLines = new ArrayList<>(0);;

    public BG12SellerTaxRepresentativePostalAddress() {
    }

    public List<BT64TaxRepresentativeAddressLine1> getBt64TaxRepresentativeAddressLines() {
        return bt64TaxRepresentativeAddressLines;
    }

    public void setBt64TaxRepresentativeAddressLines(List<BT64TaxRepresentativeAddressLine1> bt64TaxRepresentativeAddressLines) {
        this.bt64TaxRepresentativeAddressLines = bt64TaxRepresentativeAddressLines;
    }
}
