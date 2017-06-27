package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;

public class StringToUntdid1001InvoiceTypeCodeConverter extends FromStringTypeConverter<Untdid1001InvoiceTypeCode> {

    @Override public Untdid1001InvoiceTypeCode convert(final String s) {

        Integer code;
        try {
            code = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            code = null;
        }

        final Integer finalCode = code;

        Filter<Untdid1001InvoiceTypeCode> f = new Filter<Untdid1001InvoiceTypeCode>() {
            @Override public boolean apply(Untdid1001InvoiceTypeCode u) {
                return (finalCode != null && u.getCode() == finalCode) || u.getShortDescritpion().equals(s);
            }
        };
        Untdid1001InvoiceTypeCode first = Stream.create(Untdid1001InvoiceTypeCode.values()).filter(f).first();

        if(first == null) throw new IllegalArgumentException("Not found");

        return first;

    }

    @Override
    public Class<Untdid1001InvoiceTypeCode> getTargetClass() {
        return Untdid1001InvoiceTypeCode.class;
    }
}
