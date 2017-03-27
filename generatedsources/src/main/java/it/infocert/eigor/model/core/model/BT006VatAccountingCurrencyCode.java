package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

public class BT006VatAccountingCurrencyCode implements BTBG {
    private final Iso4217CurrenciesFundsCodes  currencyCode;

    public BT006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes  currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public int order() {
        return 5;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }

    @Override
    public String toString() {
        return currencyCode.toString();
    }
}
