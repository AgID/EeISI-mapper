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

            case Code82:
            case Code130:
            case Code295:
            case Code325:
            case Code326:
            case Code385:
            case Code387:
            case Code388:
            case Code390:
            case Code393:
            case Code394:
            case Code395:
            case Code575:
            case Code623:
            case Code633:
            //case Code751: - we don't have it
            case Code780:
            case Code935:
                return TipoDocumentoType.TD_01;
            case Code389:
                return TipoDocumentoType.TD_01;
            case Code381:
            case Code396:
            case Code81:
            case Code83:
            // case Code532:
            case Code262:
            case Code296:
            case Code308:
            // case Code420:
            case Code458:
                return TipoDocumentoType.TD_04;
            case Code383:
            case Code384:
            case Code80:
            case Code84:
            case Code456:
            case Code457:
                return TipoDocumentoType.TD_05;
            case Code202:
            case Code203:
            case Code204:
            case Code211:
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
