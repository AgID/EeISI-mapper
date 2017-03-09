package it.infocert.eigor.model.core.model;

public class BG002ProcessControl implements BTBG {

    public BG002ProcessControl() {
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
