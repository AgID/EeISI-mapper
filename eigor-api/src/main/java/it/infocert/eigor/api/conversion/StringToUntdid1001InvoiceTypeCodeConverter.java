package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

import java.util.Arrays;

public class StringToUntdid1001InvoiceTypeCodeConverter implements TypeConverter<String, Untdid1001InvoiceTypeCode> {

    @Override public Untdid1001InvoiceTypeCode convert(String s) {

        Integer code;
        try {
            code = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            code = null;
        }

        Integer finalCode = code;
        return Arrays.stream(Untdid1001InvoiceTypeCode.values()).filter(
                u -> (finalCode !=null && u.getCode() == finalCode) || u.getShortDescritpion().equals(s)
        ).findFirst().orElseThrow(()->new IllegalArgumentException("Not found"));
    }
}
