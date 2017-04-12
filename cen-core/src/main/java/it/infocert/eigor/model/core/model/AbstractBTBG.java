package it.infocert.eigor.model.core.model;

abstract class AbstractBTBG implements BTBG{

    private BTBG parent;

    public BTBG getParent() {
        return parent;
    }

    void setParent(BTBG parent) {
        if (this.parent != null) {
            throw new IllegalStateException("Parent already set");
        } else {
            this.parent = parent;
        }
    }
}
