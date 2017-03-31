package it.infocert.eigor.api;

import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * A fake conversion used to lay out the API general structure.
 * @see FakeToCENConversion
 */
public class FakeFromCenConverter implements FromCENConverter {

    @Override public byte[] convert(BG0000Invoice cenInvoice) {
        return "this is a fake invoice".getBytes();
    }

    @Override public boolean support(String format) {
        return "fake".equals(format);
    }

}
