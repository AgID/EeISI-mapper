package it.infocert.eigor.model.core.model;

public interface BTBG {
    public int order();

    void accept(Visitor v);
}
