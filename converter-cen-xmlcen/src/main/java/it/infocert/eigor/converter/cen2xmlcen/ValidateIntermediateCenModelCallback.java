package it.infocert.eigor.converter.cen2xmlcen;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;
import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * Conversion callback that verifies the intermediate {@link BG0000Invoice}.
 */
public class ValidateIntermediateCenModelCallback extends AbstractConversionCallback {

    private CenToXmlCenConverter cenToXmlCenConverter;

    public ValidateIntermediateCenModelCallback(EigorConfiguration configuration) {
        cenToXmlCenConverter = new CenToXmlCenConverter(configuration);
        try {
            cenToXmlCenConverter.configure();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception {
        BG0000Invoice originalCenInvoice = ctx.getToCenResult().getResult();
        if(originalCenInvoice == null) return;

        BinaryConversionResult intermediateResult = cenToXmlCenConverter.convert(originalCenInvoice);

        ctx.getToCenResult().getIssues().addAll( intermediateResult.getIssues() );
    }

}
