package it.infocert.eigor.converter.sdk;


public enum ConverterType {

    INBOUND("Converts from a specific invoice format to CEN-Core"),
    OUTBOUND("Converts from CEN-Core to a specific invoice format");


    private String description;

    ConverterType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
