package com.infocert.eigor.api;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.utils.EigorVersion;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.cen2xmlcen.DumpIntermediateCenInvoiceAsCenXmlCallback;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * API entry point that gives several services to handle invoices, transform them between several formats and check for their validity.
 *
 * <p>
 * An {@link EigorApi} instance is obtained through an {@link EigorApiBuilder} that allows to configure the API.
 * </p>
 *
 * <p>
 * {@link EigorApi} is thread safe.
 * </p>
 *
 * @see EigorApiBuilder
 */
public class EigorApi {

    private final static Logger log = LoggerFactory.getLogger(EigorApi.class);
    private final static String FULL_DATE = "yyyy-MM-dd-HH-dd-ss-SSS";
    private final EigorApiBuilder builder;

    EigorApi(EigorApiBuilder eigorApiBuilder) {
        this.builder = eigorApiBuilder;
    }

    /**
     * Convert the provided invoice assuming it is expresses in the given source format.
     *
     * <p>
     * The result contains the the converted invoice, if the conversion process was able to complete the conversion,
     * plus any error or warning that may be occurred during the process.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param targetFormat The format the invoice will be converted to.
     * @param invoice The invoice to convert.
     * @return The invoice converted in the given target format, plus any error that could have happened during the process.
     */
    public ConversionResult<byte[]> convert(final String sourceFormat, final String targetFormat, final InputStream invoice) {
        return this.convert(sourceFormat, targetFormat, invoice, null);
    }

    /**
     * Convert the provided invoice assuming it is expresses in the given source format.
     *
     * <p>
     * The result contains the the converted invoice, if the conversion process was able to complete the conversion,
     * plus any error or warning that may be occurred during the process.
     * </p>
     *
     * <p>
     *     On top of that, this method accept a number of {@link ConversionCallback callbacks}. You can use callbacks to
     *     receive notifications of noteworthy events occurring during the conversion. Please refer to the {@link ConversionCallback}
     *     documentation for a detailed explanation of what events could be listen upon.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param targetFormat The format the invoice will be converted to.
     * @param invoice The invoice to convert.
     * @param callbacks The list of callbacks to add to the transformation or null if no callbacks are needed.
     * @return The invoice converted in the given target format, plus any error that could have happened during the process.
     */
    public ConversionResult<byte[]> convert(final String sourceFormat, final String targetFormat, final InputStream invoice, ConversionCallback... callbacks) {
        log.debug(EigorVersion.getAsString());

        String stringAsDate = new SimpleDateFormat(FULL_DATE).format(new Date());

        String folderName = String.format( "conversion-%s-%s-%s-%s", stringAsDate, sourceFormat, targetFormat, (int)(Math.random()*100000));
        File outputFolderForThisTransformation = new File(builder.getOutputFolderFile(), folderName);
        outputFolderForThisTransformation.mkdirs();


        // this retrieves the converters from the relate repository, it is likely the "format" values
        // would come from a different software module, i.e. the GUI.
        ToCenConversion toCen = checkNotNull(
                builder.getConversionRepository().findConversionToCen(sourceFormat),
                "Source format '%s' not supported. Available formats are %s", sourceFormat, builder.getConversionRepository().supportedToCenFormats());
        log.debug("Converting input as '{}' to to CEN using converter '{}'.", sourceFormat, toCen.getClass().getName());

        FromCenConversion fromCen = checkNotNull(
                builder.getConversionRepository().findConversionFromCen(targetFormat),
                "Target format '%s' not supported. Available formats are %s", targetFormat, builder.getConversionRepository().supportedFromCenFormats());
        log.debug("Converting CEN to output as '{}' using converter '{}'.", targetFormat, fromCen.getClass().getName());

        ArrayList<ConversionCallback> fullListOfCallbacks = Lists.newArrayList(
                new DebugConversionCallback(outputFolderForThisTransformation),
                new DumpIntermediateCenInvoiceAsCenXmlCallback(outputFolderForThisTransformation, builder.getConfiguration()),
                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderForThisTransformation)

        );
        if(callbacks!=null && callbacks.length > 0){
            fullListOfCallbacks.addAll(Arrays.asList(callbacks) );
        }

        ObservableConversion conversion = new ObservableConversion(
                builder.getRuleRepository(),
                toCen,
                fromCen,
                invoice,
                builder.isForceConversion(),
                "invoice",
                fullListOfCallbacks);

        return conversion.conversion();

    }

    /**
     * Validates the provided invoice assuming it is expressed in the given source format.
     *
     * <p>
     * This method typically applies the XSD associated to the given source format if any and return
     * a @{@link ConversionResult result} with all the problems that the validator may have found.
     * A result that does not contain any issue means the syntax of the provided invoice is good.
     * </p>
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice The invoice which validity should be checked.
     * @return The result of the check on the invoice syntax validity.
     */
    public ConversionResult<Void> validateSyntax(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validateSyntax();
    }

    /**
     * Checks whether the provided invoice adhere to the CEN model, assuming it is expressed in the given source format.
     *
     * <p>
     * This method typically applies a schematron associated to the given source format if any and return
     * a @{@link ConversionResult result} with all the problems that the validator may have found.
     * A result that does not contain any issue means the syntax of the provided invoice is good.
     * </p>
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice The invoice which validity should be checked.
     * @return The result of the check on the invoice semantic validity.
     */
    public ConversionResult<Void> validateSemantic(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validateSemantics();
    }

    /**
     * Checks whether the provided invoice is fully valid.
     *
     * <p>
     * This method typically applies a full set of validations, XSD, Schemtaron and Italian CIUS.
     * </p>
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice The invoice which validity should be checked.
     * @return The result of the check on the invoice semantic validity.
     */
    public ConversionResult<Void> validate(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validate();
    }

    /**
     * Validate the provided xml with the provided schematron.
     * @param schemaFile A {@link File} referring to the schematron to be used for validation.
     * @param xmlToValidate An {@link InputStream} containing the XML to validate.
     * @return The result containing the validation issues, if any.
     * @throws EigorException When an unexpected error occurs.
     */
    public ConversionResult<Void> customSchSchematronValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException {

        checkNotNull(schemaFile, "Please provide a not null schematron file.");
        checkArgument(schemaFile!=null && schemaFile.isFile() && schemaFile.canRead(), "File '%s' must be a readable file, is not.", schemaFile.getAbsolutePath());
        checkNotNull(xmlToValidate);

        ErrorCode.Location location = ErrorCode.Location.CUSTOM_VALIDATORS;
        try {
            SchematronValidator schematronValidator = new SchematronValidator(
                    schemaFile, false, false, location
            );
            List<IConversionIssue> issues = schematronValidator.validate(IOUtils.toByteArray(xmlToValidate));
            return new ConversionResult<>(issues, null);
        } catch (IOException e) {
            throw new EigorException(e.getMessage(), location, ErrorCode.Action.CUSTOM_VALIDATION, ErrorCode.Error.INVALID );
        }
    }

    /**
     * Validate the provided xml with the provided XSD.
     * @param schemaFile A {@link File} referring to the schematron to be used for validation.
     * @param xmlToValidate An {@link InputStream} containing the XML to validate.
     * @return The result containing the validation issues, if any.
     * @throws EigorException When an unexpected error occurs.
     */
    public ConversionResult<Void> customXsdValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException {

        checkNotNull(schemaFile, "Please provide a not null xsd file.");
        checkArgument(schemaFile!=null && schemaFile.isFile() && schemaFile.canRead(), "File '%s' must be a readable file, is not.", schemaFile.getAbsolutePath());
        checkNotNull(xmlToValidate);

        ErrorCode.Location customValidators = ErrorCode.Location.CUSTOM_VALIDATORS;
        try {
            XSDValidator v = new PlainXSDValidator(schemaFile, customValidators);
            List<IConversionIssue> validate = v.validate(IOUtils.toByteArray(xmlToValidate));
            return new ConversionResult<>(validate, null);
        } catch (SAXException | IOException e) {
            throw new EigorException(e.getMessage(), customValidators, ErrorCode.Action.CUSTOM_VALIDATION, ErrorCode.Error.INVALID );
        }

    }

    /**
     * Return all the supported invoice source formats.
     * <p>
     *     If a value is contained in this group, it means it can be
     *     used as a source format to convert or check the invoice.
     * </p>
     */
    public Set<String> supportedSourceFormats() {
        return builder.getConversionRepository().supportedToCenFormats();
    }

    /**
     * Return all the supported invoice target formats.
     * <p>
     *     If a value is contained in this group, it means it can be
     *     used as a target format in a conversion.
     * </p>
     */
    public Set<String> supportedTargetFormats() {
        return builder.getConversionRepository().supportedToCenFormats();
    }

    /**
     * Return the detailed description of the current version.
     */
    public String getDetailedVersion() {
        return EigorVersion.getAsDetailedString();
    }

    /**
     * Return the brief description of the current version.
     */
    public String getVersion() {
        return EigorVersion.getAsString();
    }

    private ObservableValidation setupObservable(final String sourceFormat, final InputStream invoice) {
        log.debug(EigorVersion.getAsString());

        String stringAsDate = new SimpleDateFormat(FULL_DATE).format(new Date());

        String folderName = String.format( "validation-%s-%s-%s", stringAsDate, sourceFormat, (int)(Math.random()*100000));
        File outputFolderForThisTransformation = new File(builder.getOutputFolderFile(), folderName);
        outputFolderForThisTransformation.mkdirs();
        ToCenConversion toCen = builder.getConversionRepository().findConversionToCen(sourceFormat);
        ConversionCallback callback = new DebugConversionCallback(
                outputFolderForThisTransformation
        );

        return new ObservableValidation(
                invoice,
                toCen,
                "invoice",
                Lists.newArrayList(callback),
                builder.getRuleRepository()
        );
    }
}
