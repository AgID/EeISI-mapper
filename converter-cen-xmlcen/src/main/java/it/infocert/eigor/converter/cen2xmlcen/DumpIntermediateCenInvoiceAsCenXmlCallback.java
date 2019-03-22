package it.infocert.eigor.converter.cen2xmlcen;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;
import it.infocert.eigor.api.utils.ConversionIssueUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.io.FileUtils.writeStringToFile;

/**
 * Conversion callback that writes out the intermediate CEN invoice in CEN-XML format.
 */
public class DumpIntermediateCenInvoiceAsCenXmlCallback extends AbstractConversionCallback {

    private final File outputFolderFile;
    private CenToXmlCenConverter cenToXmlCenConverter;
    private final boolean dumpIssues;

    public DumpIntermediateCenInvoiceAsCenXmlCallback(File outputFolderFile, CenToXmlCenConverter cenToXmlConverter) {
        this(outputFolderFile, cenToXmlConverter, false);
    }

    public DumpIntermediateCenInvoiceAsCenXmlCallback(File outputFolderFile, CenToXmlCenConverter cenToXmlConverter, boolean dumpIssues) {
        cenToXmlCenConverter = checkNotNull(cenToXmlConverter);
        this.outputFolderFile = outputFolderFile;
        try {
            cenToXmlCenConverter.configure();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
        this.dumpIssues = dumpIssues;
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

        writeStringToFile(
                new File(outputFolderFile, "invoice-cen.xml"),
                new String( result.getResult()) ,
                StandardCharsets.UTF_8);

        if(dumpIssues){
            writeStringToFile(
                    new File(outputFolderFile, "invoice-cen-issues.csv"),
                    ConversionIssueUtils.toCsv( result.getIssues() ),
                    StandardCharsets.UTF_8);
        }

    }

}
