package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.dump.CsvDumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Conversion callback that writes out the intermediate CEN invoice in CSV format.
 */
public class DumpIntermediateCenInvoiceAsCsvCallback extends AbstractConversionCallback {

    private final File outputFolderFile;

    public DumpIntermediateCenInvoiceAsCsvCallback(File outputFolderFile) {
        this.outputFolderFile = outputFolderFile;
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
        Visitor v = new CsvDumpVisitor();
        cenInvoice.accept(v);
        FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString(), StandardCharsets.UTF_8);
    }

}
