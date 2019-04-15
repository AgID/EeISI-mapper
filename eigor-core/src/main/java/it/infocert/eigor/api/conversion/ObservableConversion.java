package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the core algorithm that converts a given invoice to the desired format.
 * <h2>Callbacks</h2>
 * <p>
 * It is possible to attach an unlimited number of callbacks that get informed
 * about meaningful events such, among others:
 * <ul>
 * <li>a conversion being started,</li>
 * <li>a conversion completed with errors</li>
 * <li>etc...</li>
 * </ul>
 * Just provide them in the constructor.
 * For details, please check {@link ConversionCallback}.
 * </p>
 */
public class ObservableConversion extends AbstractObservable {

    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final String invoiceFileName;
    private byte[] invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @param toCenConversion
     * @param fromCenConversion
     * @param invoiceInSourceFormat
     * @param forceConversion
     * @param invoiceFileName
     * @param callbacks
     */
    public ObservableConversion(ToCenConversion toCenConversion, FromCenConversion fromCenConversion, InputStream invoiceInSourceFormat, boolean forceConversion, String invoiceFileName, ConversionCallback... callbacks) {
        this(
                toCenConversion,
                fromCenConversion,
                invoiceInSourceFormat,
                forceConversion,
                invoiceFileName,
                Arrays.asList( callbacks ));
    }

    /**
     * @param toCenConversion
     * @param fromCenConversion
     * @param invoiceInSourceFormat
     * @param forceConversion
     * @param invoiceFileName
     * @param callbacks
     */
    public ObservableConversion(ToCenConversion toCenConversion, FromCenConversion fromCenConversion, InputStream invoiceInSourceFormat, boolean forceConversion, String invoiceFileName, List<ConversionCallback> callbacks) {
        super(checkNotNull(callbacks));
        this.toCen = checkNotNull(toCenConversion);
        this.fromCen = checkNotNull(fromCenConversion);

        checkNotNull(invoiceInSourceFormat, "The binary version of the invoice is mandatory.");
        try {
            this.invoiceInSourceFormat = IOUtils.toByteArray(invoiceInSourceFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.forceConversion = forceConversion;
        checkNotNull(invoiceFileName);
        checkArgument(!invoiceFileName.isEmpty());
        this.invoiceFileName = invoiceFileName;
    }

    public BinaryConversionResult conversion() {

        // Whether to go on to the next step. When false, we should stop executing subsequent operations.
        boolean keepOnGoing = true;

        // The intermediate CEN invoice.
        BG0000Invoice cenInvoice = null;

        // The final converted invoice
        BinaryConversionResult fromCenResult = null;

        final ConversionContext ctx = new ConversionContext();
        ctx.setForceConversion(forceConversion);
        ctx.setInvoiceInSourceFormat(invoiceInSourceFormat);
        ctx.setInvoiceFileName(invoiceFileName);
        ctx.setTargetInvoiceExtension(fromCen.extension());

        List<IConversionIssue> issues = new ArrayList<>();


        try {
            // conversion start
            fireOnStartingConverionEvent(ctx);

            // 1st step XML -> CEN
            fireOnStartingToCenTranformationEvent(ctx);
            ConversionResult<BG0000Invoice> toCenResult = toCen.convert(new ByteArrayInputStream(invoiceInSourceFormat));
            ctx.setToCenResult(toCenResult);
            if (!toCenResult.hasIssues()) {
                fireOnSuccessfullToCenTranformationEvent(ctx);
            } else {
                fireOnFailedToCenConversion(ctx);
                issues.addAll(toCenResult.getIssues());
                if (!forceConversion)
                    keepOnGoing = false;
            }

            // 2nd step CEN verification
            if (keepOnGoing) {

                boolean errorsHappended = false;

                toCenResult.clearIssues();
                fireOnStartingVerifyingCenRules(ctx);
                if(!toCenResult.getIssues().isEmpty()){
                    issues.addAll(toCenResult.getIssues());
                    errorsHappended = true;
                }

                cenInvoice = toCenResult.getResult();


                if (!forceConversion && errorsHappended)
                    keepOnGoing = false;
            }

            // 3rd step CEN -> XML
            if (keepOnGoing) {
                fireOnStartingFromCenTransformation(ctx);
                fromCenResult = fromCen.convert(cenInvoice);
                ctx.setFromCenResult(fromCenResult);
                if (!fromCenResult.hasIssues()) {
                    fireOnSuccessfullFromCenTransformation(ctx);
                } else {
                    fireOnFailedFromCenTransformation(ctx);
                    issues.addAll(fromCenResult.getIssues());
                }
            }
        } catch (EigorException e) {
            issues.add(ConversionIssue.newError(e));
            fireOnUnexpectedException(e, ctx);
        }

        // anyhow, we inform the listeners we completed the transformation
        fireOnTerminatedConversion(ctx);

        return new BinaryConversionResult(fromCenResult != null ? fromCenResult.getResult() : null, issues);

    }

}
