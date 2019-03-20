package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.ToStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.fattpa.commons.models.TipoDocumentoType;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class Untdid1001InvoiceTypeCodeToItalianCodeStringConverter extends ToStringTypeConverter<Untdid1001InvoiceTypeCode> {

    private final TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> delegate;

    Untdid1001InvoiceTypeCodeToItalianCodeStringConverter() {
        delegate = Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter.newConverter();
    }

    @Override
    public String convert(Untdid1001InvoiceTypeCode untdid1001InvoiceTypeCode) {
        TipoDocumentoType converted;
        try {
            converted = delegate.convert(untdid1001InvoiceTypeCode);
        } catch (ConversionFailedException e) {
            throw new RuntimeException(e);
        }
        return converted == null ? "" : converted.value();
    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getSourceClass() {
        return Untdid1001InvoiceTypeCode.class;
    }

    public static TypeConverter newConverter() {
        return new Untdid1001InvoiceTypeCodeToItalianCodeStringConverter();
    }
}
