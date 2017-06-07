package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.util.ArrayList;

/**
 * A fake conversion used to lay out the API general structure.
 *
 * @see FakeToCenConversion
 */
public class FakeFromCenConversion implements FromCenConversion {

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) {
        BinaryConversionResult binaryConversionResult = new BinaryConversionResult("this is a fake invoice".getBytes(), new ArrayList<ConversionIssue>());
        return binaryConversionResult;
    }

    @Override
    public boolean support(String format) {
        return "fake".equals(format);
    }

    @Override
    public String getSupportedFormats() {
        return "fake";
    }

    @Override
    public String extension() {
        return "fake";
    }

}
