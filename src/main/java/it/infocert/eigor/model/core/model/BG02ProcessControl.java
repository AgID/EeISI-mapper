package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.dump.Visitor;

public class BG02ProcessControl implements BTBG {

    public BG02ProcessControl() {
    }

    @Override
    public int order() {
        return 23;
    }

    @Override
    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }
}
