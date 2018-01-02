package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class Untdid1001InvoiceTypeCodesToStringConverter extends ToStringTypeConverter<Untdid1001InvoiceTypeCode> {

    Untdid1001InvoiceTypeCodesToStringConverter() {
    }

    @Override
    public String convert(Untdid1001InvoiceTypeCode untdid1001InvoiceTypeCode) {
        return String.valueOf(untdid1001InvoiceTypeCode.getCode());
    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getSourceClass() {
        return Untdid1001InvoiceTypeCode.class;
    }

    public static TypeConverter<Untdid1001InvoiceTypeCode, String> newConverter() {
        return new Untdid1001InvoiceTypeCodesToStringConverter();
    }
}
