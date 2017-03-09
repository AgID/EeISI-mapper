package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.dump.Visitor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BT64TaxRepresentativeAddressLine1 implements BTBG {
    @Override
    public int order() {
        return 77;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }
}
