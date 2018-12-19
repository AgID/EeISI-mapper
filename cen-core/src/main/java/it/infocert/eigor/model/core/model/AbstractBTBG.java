package it.infocert.eigor.model.core.model;

public abstract class AbstractBTBG extends BTBG {

    private AbstractBTBG parent;

    public AbstractBTBG getParent() {
        return parent;
    }

    void setParent(AbstractBTBG parent) {
        if (this.parent != null) {
            throw new IllegalStateException("Parent already set");
        } else {
            this.parent = parent;
        }
    }
}
