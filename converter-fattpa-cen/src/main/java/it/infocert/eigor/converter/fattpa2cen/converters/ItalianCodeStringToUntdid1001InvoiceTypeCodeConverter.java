package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class ItalianCodeStringToUntdid1001InvoiceTypeCodeConverter implements TypeConverter<String,Untdid1001InvoiceTypeCode> {

    ItalianCodeStringToUntdid1001InvoiceTypeCodeConverter() {
    }

    @Override public Untdid1001InvoiceTypeCode convert(String stringCode) {
        switch (stringCode) {
            case "TD01":
            case "TD06":
                return Untdid1001InvoiceTypeCode.Code380;
            case "TD04":
                return Untdid1001InvoiceTypeCode.Code381;
            case "TD05":
                return Untdid1001InvoiceTypeCode.Code383;
            case "TD02":
            case "TD03":
                return Untdid1001InvoiceTypeCode.Code386;
            default:
                return null;
        }
    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getTargetClass() {
        return Untdid1001InvoiceTypeCode.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    public static TypeConverter<String,Untdid1001InvoiceTypeCode> newConverter() {
        return new ItalianCodeStringToUntdid1001InvoiceTypeCodeConverter();
    }
}
