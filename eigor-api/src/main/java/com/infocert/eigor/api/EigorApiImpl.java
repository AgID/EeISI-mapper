package com.infocert.eigor.api;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.utils.EigorVersion;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.cen2xmlcen.DumpIntermediateCenInvoiceAsCenXmlCallback;
import it.infocert.eigor.org.springframework.core.io.FileSystemResource;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import sun.nio.ch.ChannelInputStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * API entry point that gives several services to handle invoices, transform them between several formats and check for their validity.
 *
 * <p>
 * An {@link EigorApiImpl} instance is obtained through an {@link EigorApiBuilder} that allows to configure the API.
 * </p>
 *
 * <p>
 * {@link EigorApiImpl} is thread safe.
 * </p>
 *
 * @see EigorApiBuilder
 */
public class EigorApiImpl implements EigorApi {

    private final static Logger log = LoggerFactory.getLogger(EigorApiImpl.class);
    private final static String FULL_DATE = "yyyy-MM-dd-HH-dd-ss-SSS";
    private final EigorApiBuilder builder;

    EigorApiImpl(EigorApiBuilder eigorApiBuilder) {
        this.builder = eigorApiBuilder;
    }

    @Override
    public ConversionResult<byte[]> convert(final String sourceFormat, final String targetFormat, final InputStream invoice) {
        return this.convert(sourceFormat, targetFormat, invoice, "invoice", null);
    }

    @Override
    public ConversionResult<byte[]> convert(final String sourceFormat, final String targetFormat, final InputStream invoice, final String invoiceName, ConversionCallback... callbacks) {
        return this.convert(sourceFormat, targetFormat, invoice, invoiceName, new ConversionPreferences(), callbacks);
    }

    @Override
    public ConversionResult<byte[]> convert(final String sourceFormat, final String targetFormat, final InputStream invoice, final String invoiceName, ConversionPreferences preferences, ConversionCallback... callbacks) {
        log.debug(EigorVersion.getAsString());

        String effectiveSourceFormat = sourceFormat;

        byte[] bytes = null;
        if ("ubl".equals(sourceFormat)) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(invoice, baos);
                bytes = baos.toByteArray();
                String invoiceString = new String(bytes);
                if (invoiceString.contains("<CreditNote")) {
                    effectiveSourceFormat = "ublcn";
                    log.warn("Source format is ubl but the content of the file is a CreditNote instead an invoice, proceeding with ublcn source format");
                }
            } catch (IOException e) {
                log.warn("Can't read invoice and determinate if it is a CreditNote, proceeding with ubl source format");
            }
        }

        String stringAsDate = new SimpleDateFormat(FULL_DATE).format(new Date());

        String folderName = String.format("conversion-%s-%s-%s-%s", stringAsDate, effectiveSourceFormat, targetFormat, (int) (Math.random() * 100000));
        File outputFolderForThisTransformation = new File(builder.getOutputFolderFile(), folderName);
        outputFolderForThisTransformation.mkdirs();


        // this retrieves the converters from the relate repository, it is likely the "format" values
        // would come from a different software module, i.e. the GUI.
        ToCenConversion toCen = checkNotNull(
                builder.getConversionRepository().findConversionToCen(effectiveSourceFormat),
                "Source format '%s' not supported. Available formats are %s", effectiveSourceFormat, builder.getConversionRepository().supportedToCenFormats());
        log.debug("Converting input as '{}' to to CEN using converter '{}'.", effectiveSourceFormat, toCen.getClass().getName());

        FromCenConversion fromCen = checkNotNull(
                builder.getConversionRepository().findConversionFromCen(targetFormat),
                "Target format '%s' not supported. Available formats are %s", targetFormat, builder.getConversionRepository().supportedFromCenFormats());
        log.debug("Converting CEN to output as '{}' using converter '{}'.", targetFormat, fromCen.getClass().getName());

        ArrayList<ConversionCallback> fullListOfCallbacks = Lists.newArrayList(
                new DebugConversionCallback(outputFolderForThisTransformation),
                new DumpIntermediateCenInvoiceAsCenXmlCallback(outputFolderForThisTransformation, builder.getCen2XmlCen(), false),
                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderForThisTransformation)

        );
        if (callbacks != null && callbacks.length > 0) {
            fullListOfCallbacks.addAll(Arrays.asList(callbacks));
        }
        if (preferences.validateIntermediateCen()) {
            fullListOfCallbacks.add(new ValidateIntermediateCenModelCallback(builder.getCen2XmlCen()));
        }

        ObservableConversion conversion = new ObservableConversion(
                builder.getRuleRepository(),
                toCen,
                fromCen,
                (bytes == null) ? invoice : new ByteArrayInputStream(bytes),
                preferences.forceConversion() != null ? preferences.forceConversion() : builder.isForceConversion(),
                invoiceName,
                fullListOfCallbacks);

        return conversion.conversion();

    }

    @Override
    public ConversionResult<Void> validateSyntax(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validateSyntax();
    }

    @Override
    public ConversionResult<Void> validateSemantic(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validateSemantics();
    }

    @Override
    public ConversionResult<Void> validate(final String sourceFormat, final InputStream invoice) {
        return setupObservable(sourceFormat, invoice).validate();
    }

    @Override
    public ConversionResult<Void> customSchSchematronValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException {

        checkNotNull(schemaFile, "Please provide a not null schematron file.");
        checkArgument(schemaFile != null && schemaFile.isFile() && schemaFile.canRead(), "File '%s' must be a readable file, is not.", schemaFile.getAbsolutePath());
        checkNotNull(xmlToValidate);

        ErrorCode.Location location = ErrorCode.Location.CUSTOM_VALIDATORS;
        try {
            SchematronValidator schematronValidator = new SchematronValidator(new FileSystemResource(schemaFile), false, false, location);
            List<IConversionIssue> issues = schematronValidator.validate(IOUtils.toByteArray(xmlToValidate));
            return new ConversionResult<>(issues, null);
        } catch (IOException e) {
            throw new EigorException(e.getMessage(), location, ErrorCode.Action.CUSTOM_VALIDATION, ErrorCode.Error.INVALID);
        }
    }

    @Override
    public ConversionResult<Void> customXsdValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException {

        checkNotNull(schemaFile, "Please provide a not null xsd file.");
        checkArgument(schemaFile != null && schemaFile.isFile() && schemaFile.canRead(), "File '%s' must be a readable file, is not.", schemaFile.getAbsolutePath());
        checkNotNull(xmlToValidate);

        ErrorCode.Location customValidators = ErrorCode.Location.CUSTOM_VALIDATORS;
        try {
            XSDValidator v = new PlainXSDValidator(schemaFile, customValidators);
            List<IConversionIssue> validate = v.validate(IOUtils.toByteArray(xmlToValidate));
            return new ConversionResult<>(validate, null);
        } catch (SAXException | IOException e) {
            throw new EigorException(e.getMessage(), customValidators, ErrorCode.Action.CUSTOM_VALIDATION, ErrorCode.Error.INVALID);
        }

    }

    @Override
    public Set<String> supportedSourceFormats() {
        return builder.getConversionRepository().supportedToCenFormats();
    }

    @Override
    public Set<String> supportedTargetFormats() {
        return builder.getConversionRepository().supportedFromCenFormats();
    }

    @Override
    public String getDetailedVersion() {
        return EigorVersion.getAsDetailedString();
    }

    @Override
    public String getVersion() {
        return EigorVersion.getAsString();
    }

    private ObservableValidation setupObservable(final String sourceFormat, final InputStream invoice) {
        log.debug(EigorVersion.getAsString());

        String stringAsDate = new SimpleDateFormat(FULL_DATE).format(new Date());

        String folderName = String.format("validation-%s-%s-%s", stringAsDate, sourceFormat, (int) (Math.random() * 100000));
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
