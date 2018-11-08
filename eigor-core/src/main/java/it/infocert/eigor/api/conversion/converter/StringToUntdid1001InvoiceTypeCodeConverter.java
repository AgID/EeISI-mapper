package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StringToUntdid1001InvoiceTypeCodeConverter extends FromStringTypeConverter<Untdid1001InvoiceTypeCode> {

    public static TypeConverter<String, Untdid1001InvoiceTypeCode> newConverter() {
        return new StringToUntdid1001InvoiceTypeCodeConverter();
    }

    private StringToUntdid1001InvoiceTypeCodeConverter() {
    }

    @Override public Untdid1001InvoiceTypeCode convert(final String s) {

        Integer code;
        try {
            code = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            code = null;
        }

        final Integer finalCode = code;



        Predicate<Untdid1001InvoiceTypeCode> f = u -> (finalCode != null && u.getCode() == finalCode) || u.getShortDescritpion().equals(s);
        Untdid1001InvoiceTypeCode first = Arrays.stream(Untdid1001InvoiceTypeCode.values()).filter(f).findFirst().orElse(null);

        if(first == null) throw new IllegalArgumentException("Not found");

        return first;

    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getTargetClass() {
        return Untdid1001InvoiceTypeCode.class;
    }


}
