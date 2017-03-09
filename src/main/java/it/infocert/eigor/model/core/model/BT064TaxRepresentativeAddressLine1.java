package it.infocert.eigor.model.core.model;

import static java.util.Comparator.comparing;

public class BT064TaxRepresentativeAddressLine1 implements BTBG {
    @Override
    public int order() {
        return 77;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }
}
