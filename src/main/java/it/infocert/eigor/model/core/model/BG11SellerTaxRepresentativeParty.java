package it.infocert.eigor.model.core.model;

import java.util.List;

public class BG11SellerTaxRepresentativeParty {
    private List<BG12SellerTaxRepresentativePostalAddress> bg12SellerTaxRepresentativePostalAddresses;

    public BG11SellerTaxRepresentativeParty() {
    }

    public List<BG12SellerTaxRepresentativePostalAddress> getBg12SellerTaxRepresentativePostalAddresses() {
        return bg12SellerTaxRepresentativePostalAddresses;
    }

    public void setBg12SellerTaxRepresentativePostalAddresses(List<BG12SellerTaxRepresentativePostalAddress> bg12SellerTaxRepresentativePostalAddresses) {
        this.bg12SellerTaxRepresentativePostalAddresses = bg12SellerTaxRepresentativePostalAddresses;
    }
}
