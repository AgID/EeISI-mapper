package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.converter.cen2fattpa.models.TipoDocumentoType;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter implements TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> {

    Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter() {
    }

    @Override
    public TipoDocumentoType convert(Untdid1001InvoiceTypeCode typeCode) {
        switch (typeCode) {
            case Code380:
                return TipoDocumentoType.TD_01;
            case Code389:
                return TipoDocumentoType.TD_01;
            case Code381:
                return TipoDocumentoType.TD_04;
            case Code383:
                return TipoDocumentoType.TD_05;
            case Code386:
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
