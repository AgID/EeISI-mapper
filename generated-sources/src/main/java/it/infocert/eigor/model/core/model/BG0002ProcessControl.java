package it.infocert.eigor.model.core.model;

public class BG0002ProcessControl implements BTBG {

    public BG0002ProcessControl() {
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
