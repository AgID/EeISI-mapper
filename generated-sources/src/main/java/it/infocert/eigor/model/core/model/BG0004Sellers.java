package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.model.*;
import java.util.ArrayList;
import java.util.List;

public class BG0004Sellers {
    private List<BT0027SellerName> bt0027SellerNames = new ArrayList<>(0);
    private List<BT0028SellerTradingName> bt0028SellerTradingNames = new ArrayList<>(0);
    private List<BT0029SellerIdentifier> bt0029SellerIdentifiers = new ArrayList<>(0);
    private List<BT0031SellerVatIdentifier> bt0031SellerVatIdentifiers = new ArrayList<>(0);
    private List<BT0032SellerTaxRegistrationIdentifier> bt0032SellerTaxRegistrationIdentifiers = new ArrayList<>(0);

    public List<BT0027SellerName> getBt0027SellerNames() {
        return bt0027SellerNames;
    }

    public void setBt0027SellerNames(List<BT0027SellerName> bt0027SellerNames) {
        this.bt0027SellerNames = bt0027SellerNames;
    }

    public List<BT0028SellerTradingName> getBt0028SellerTradingNames() {
        return bt0028SellerTradingNames;
    }

    public void setBt0028SellerTradingNames(List<BT0028SellerTradingName> bt0028SellerTradingNames) {
        this.bt0028SellerTradingNames = bt0028SellerTradingNames;
    }

    public List<BT0029SellerIdentifier> getBt0029SellerIdentifiers() {
        return bt0029SellerIdentifiers;
    }

    public void setBt0029SellerIdentifiers(List<BT0029SellerIdentifier> bt0029SellerIdentifiers) {
        this.bt0029SellerIdentifiers = bt0029SellerIdentifiers;
    }

    public List<BT0031SellerVatIdentifier> getBt0031SellerVatIdentifiers() {
        return bt0031SellerVatIdentifiers;
    }

    public void setBt0031SellerVatIdentifiers(List<BT0031SellerVatIdentifier> bt0031SellerVatIdentifiers) {
        this.bt0031SellerVatIdentifiers = bt0031SellerVatIdentifiers;
    }

    public List<BT0032SellerTaxRegistrationIdentifier> getBt0032SellerTaxRegistrationIdentifiers() {
        return bt0032SellerTaxRegistrationIdentifiers;
    }

    public void setBt0032SellerTaxRegistrationIdentifiers(List<BT0032SellerTaxRegistrationIdentifier> bt0032SellerTaxRegistrationIdentifiers) {
        this.bt0032SellerTaxRegistrationIdentifiers = bt0032SellerTaxRegistrationIdentifiers;
    }
}
