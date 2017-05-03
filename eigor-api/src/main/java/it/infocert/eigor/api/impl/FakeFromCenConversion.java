package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * A fake conversion used to lay out the API general structure.
 *
 * @see FakeToCenConversion
 */
public class FakeFromCenConversion implements FromCenConversion {

    @Override
    public ConversionResult convert(BG0000Invoice invoice) {
        ConversionResult conversionResult = new ConversionResult();
        conversionResult.setResult("this is a fake invoice".getBytes());
        conversionResult.setSuccessful(true);
        return conversionResult;
    }

    @Override
    public boolean support(String format) {
        return "fake".equals(format);
    }

    @Override
    public String getSupportedFormats() {
        return "fake";
    }

}
