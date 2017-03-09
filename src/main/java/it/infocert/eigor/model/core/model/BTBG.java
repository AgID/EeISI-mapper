package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.dump.Visitor;

public interface BTBG {
    public int order();

    void accept(Visitor v);
}
