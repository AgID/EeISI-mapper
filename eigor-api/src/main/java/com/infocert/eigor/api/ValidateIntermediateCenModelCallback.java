package com.infocert.eigor.api;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;
import it.infocert.eigor.converter.cen2xmlcen.CenToXmlCenConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * Conversion callback that verifies the intermediate {@link BG0000Invoice}.
 */
class ValidateIntermediateCenModelCallback extends AbstractConversionCallback {

    private CenToXmlCenConverter cenToXmlCenConverter;

    ValidateIntermediateCenModelCallback(CenToXmlCenConverter cen2XmlCen) {
        cenToXmlCenConverter = cen2XmlCen;
    }

    @Override
    public void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception {
        BG0000Invoice originalCenInvoice = ctx.getToCenResult().getResult();
        if(originalCenInvoice == null) return;

        BinaryConversionResult intermediateResult = cenToXmlCenConverter.convert(originalCenInvoice);

        ctx.getToCenResult().addIssues( intermediateResult.getIssues() );
    }

}
