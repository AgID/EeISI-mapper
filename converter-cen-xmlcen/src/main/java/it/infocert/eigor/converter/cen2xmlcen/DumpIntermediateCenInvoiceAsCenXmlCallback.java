package it.infocert.eigor.converter.cen2xmlcen;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Conversion callback that writes out the intermediate CEN invoice in CEN-XML format.
 */
public class DumpIntermediateCenInvoiceAsCenXmlCallback extends AbstractConversionCallback {

    private final File outputFolderFile;
    private CenToXmlCenConverter cenToXmlCenConverter;

    @Deprecated
    public DumpIntermediateCenInvoiceAsCenXmlCallback(File outputFolderFile, EigorConfiguration configuration) {
        cenToXmlCenConverter = new CenToXmlCenConverter(configuration);
        this.outputFolderFile = outputFolderFile;
        try {
            cenToXmlCenConverter.configure();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public DumpIntermediateCenInvoiceAsCenXmlCallback(File outputFolderFile, CenToXmlCenConverter cenToXmlConverter) {
        cenToXmlCenConverter = checkNotNull(cenToXmlConverter);
        this.outputFolderFile = outputFolderFile;
        try {
            cenToXmlCenConverter.configure();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception {
        writeCenInvoice(ctx.getToCenResult().getResult(), outputFolderFile);
    }

    @Override
    public void onFailedToCenConversion(ConversionContext ctx) throws Exception {
        writeCenInvoice(ctx.getToCenResult().getResult(), outputFolderFile);
    }

    private void writeCenInvoice(BG0000Invoice cenInvoice, File outputFolderFile) throws IOException {
        BinaryConversionResult result;
        try {
            result = cenToXmlCenConverter.convert(cenInvoice);
        } catch (SyntaxErrorInInvoiceFormatException e) {
            throw new RuntimeException(e);
        }
        FileUtils.writeStringToFile(
                new File(outputFolderFile, "invoice-cen.xml"),
                new String( result.getResult()) ,
                StandardCharsets.UTF_8);
    }

}
