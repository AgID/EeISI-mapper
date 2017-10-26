package it.infocert.eigor.model.core.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractBTBG extends BTBG {

    @Nullable private AbstractBTBG parent;

    @Nullable public AbstractBTBG getParent() {
        return parent;
    }

    void setParent(@Nullable AbstractBTBG parent) {
        if (this.parent != null) {
            throw new IllegalStateException("Parent already set");
        } else {
            this.parent = parent;
        }
    }
}
