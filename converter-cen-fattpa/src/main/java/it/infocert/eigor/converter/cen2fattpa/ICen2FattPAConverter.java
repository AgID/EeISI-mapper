package it.infocert.eigor.converter.cen2fattpa;

public interface ICen2FattPAConverter {

    void copyRequiredOne2OneFields();
    void copyOptionalOne2OneFields();

    void computeMultipleCenElements2FpaField();

    void transformFpaFields();

}
