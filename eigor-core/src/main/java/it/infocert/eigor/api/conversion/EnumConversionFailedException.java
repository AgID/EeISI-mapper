package it.infocert.eigor.api.conversion;

/** A conversion error occurred while converting to an enum. */
public class EnumConversionFailedException extends ConversionFailedException {

    public EnumConversionFailedException(String s) {
        super(s);
    }

    public EnumConversionFailedException(Exception e) {
        super(e);
    }
}
