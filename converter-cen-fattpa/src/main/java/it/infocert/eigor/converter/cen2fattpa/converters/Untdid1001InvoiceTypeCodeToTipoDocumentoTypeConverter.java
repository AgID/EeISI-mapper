package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.converter.cen2fattpa.models.TipoDocumentoType;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter implements TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> {

    Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter() {
    }

    @Override
    public TipoDocumentoType convert(Untdid1001InvoiceTypeCode untdid1001InvoiceTypeCode) {
        switch (untdid1001InvoiceTypeCode.getCode()) {
            case 380:
            case 389:
                return TipoDocumentoType.TD_01;
            case 381:
                return TipoDocumentoType.TD_04;
            case 383:
                return TipoDocumentoType.TD_05;
            case 386:
                return TipoDocumentoType.TD_02;
            default:
                return null;
        }
    }

    @Override
    public Class<TipoDocumentoType> getTargetClass() {
        return TipoDocumentoType.class;
    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getSourceClass() {
        return Untdid1001InvoiceTypeCode.class;
    }

    public static TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> newConverter() {
        return new Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter();
    }
}
