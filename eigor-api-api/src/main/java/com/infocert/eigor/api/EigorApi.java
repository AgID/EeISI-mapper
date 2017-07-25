package com.infocert.eigor.api;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.DebugConversionCallback;
import it.infocert.eigor.api.conversion.ObservableConversion;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Convert a given invoice into a given format.
 */
public class EigorApi {

    private final EigorApiBuilder builder;

    EigorApi(EigorApiBuilder eigorApiBuilder) {
        this.builder = eigorApiBuilder;
    }

    /**
     * Convert the provided invoice assuming it is expresses in the given source format.
     * @param sourceFormat The format of the invoice to be converted.
     * @param targetFormat The format the invoice will be converted to.
     * @param invoice The invoice to convert.
     * @return The invoice converted in the given target format.
     */
    public ConversionResult<byte[]> convert(String sourceFormat, String targetFormat, InputStream invoice) {

        String stringAsDate = new SimpleDateFormat("yyyy-mm-dd-HH-MM-ss-SSS").format(new Date());

        String folderName = String.format( "conversion-%s-%s-%s-%s", stringAsDate, sourceFormat, targetFormat, (int)(Math.random()*100000));
        File outputFolderForThisTransformation = new File(builder.getOutputFolderFile(), folderName);
        outputFolderForThisTransformation.mkdirs();

        ObservableConversion.ConversionCallback callback = new DebugConversionCallback(
                outputFolderForThisTransformation
        );

        // this retrieves the converters from the relate repository, it is likely the "format" values
        // would come from a different software module, i.e. the GUI.
        ToCenConversion toCen = builder.getConversionRepository().findConversionToCen(sourceFormat);
        FromCenConversion fromCen = builder.getConversionRepository().findConversionFromCen(targetFormat);

        return new ObservableConversion(
                builder.getRuleRepository(),
                toCen,
                fromCen,
                invoice,
                false,
                "invoice",
                Arrays.asList(callback)).conversion();

    }
}
