package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.EigorException;
import it.infocert.eigor.api.conversion.ConversionCallback;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public interface EigorApi {
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
     * @param invoice      The invoice to convert.
     * @return The invoice converted in the given target format, plus any error that could have happened during the process.
     */
    ConversionResult<byte[]> convert(String sourceFormat, String targetFormat, InputStream invoice);

    /**
     * Convert the provided invoice assuming it is expresses in the given source format.
     *
     * <p>
     * The result contains the the converted invoice, if the conversion process was able to complete the conversion,
     * plus any error or warning that may be occurred during the process.
     * </p>
     *
     * <p>
     * On top of that, this method accept a number of {@link ConversionCallback callbacks}. You can use callbacks to
     * receive notifications of noteworthy events occurring during the conversion. Please refer to the {@link ConversionCallback}
     * documentation for a detailed explanation of what events could be listen upon.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param targetFormat The format the invoice will be converted to.
     * @param invoice      The invoice to convert.
     * @param callbacks    The list of callbacks to add to the transformation or null if no callbacks are needed.
     * @return The invoice converted in the given target format, plus any error that could have happened during the process.
     */
    ConversionResult<byte[]> convert(String sourceFormat, String targetFormat, InputStream invoice, String invoiceName, ConversionCallback... callbacks);

    /**
     * Convert the provided invoice assuming it is expresses in the given source format.
     *
     * <p>
     * The result contains the the converted invoice, if the conversion process was able to complete the conversion,
     * plus any error or warning that may be occurred during the process.
     * </p>
     *
     * <p>
     * On top of that, this method accept a number of {@link ConversionCallback callbacks}. You can use callbacks to
     * receive notifications of noteworthy events occurring during the conversion. Please refer to the {@link ConversionCallback}
     * documentation for a detailed explanation of what events could be listen upon.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param targetFormat The format the invoice will be converted to.
     * @param invoice      The invoice to convert.
     * @param conversionPreferences
     *                     Preferences that allows to tweak how the conversion is performed.
     * @param callbacks    The list of callbacks to add to the transformation or null if no callbacks are needed.
     * @return The invoice converted in the given target format, plus any error that could have happened during the process.
     */
    ConversionResult<byte[]> convert(String sourceFormat, String targetFormat, InputStream invoice, String invoiceName, ConversionPreferences conversionPreferences, ConversionCallback... callbacks);

    /**
     * Validates the provided invoice assuming it is expressed in the given source format.
     *
     * <p>
     * This method typically applies the XSD associated to the given source format if any and return
     * a @{@link ConversionResult result} with all the problems that the validator may have found.
     * A result that does not contain any issue means the syntax of the provided invoice is good.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice      The invoice which validity should be checked.
     * @return The result of the check on the invoice syntax validity.
     */
    ConversionResult<Void> validateSyntax(String sourceFormat, InputStream invoice);

    /**
     * Checks whether the provided invoice adhere to the CEN model, assuming it is expressed in the given source format.
     *
     * <p>
     * This method typically applies a schematron associated to the given source format if any and return
     * a @{@link ConversionResult result} with all the problems that the validator may have found.
     * A result that does not contain any issue means the syntax of the provided invoice is good.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice      The invoice which validity should be checked.
     * @return The result of the check on the invoice semantic validity.
     */
    ConversionResult<Void> validateSemantic(String sourceFormat, InputStream invoice);

    /**
     * Checks whether the provided invoice is fully valid.
     *
     * <p>
     * This method typically applies a full set of validations, XSD, Schemtaron and Italian CIUS.
     * </p>
     *
     * @param sourceFormat The format of the invoice to be converted.
     * @param invoice      The invoice which validity should be checked.
     * @return The result of the check on the invoice semantic validity.
     */
    ConversionResult<Void> validate(String sourceFormat, InputStream invoice);

    /**
     * Validate the provided xml with the provided schematron.
     *
     * @param schemaFile    A {@link File} referring to the schematron to be used for validation.
     * @param xmlToValidate An {@link InputStream} containing the XML to validate.
     * @return The result containing the validation issues, if any.
     * @throws EigorException When an unexpected error occurs.
     */
    ConversionResult<Void> customSchSchematronValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException;

    /**
     * Validate the provided xml with the provided XSD.
     *
     * @param schemaFile    A {@link File} referring to the schematron to be used for validation.
     * @param xmlToValidate An {@link InputStream} containing the XML to validate.
     * @return The result containing the validation issues, if any.
     * @throws EigorException When an unexpected error occurs.
     */
    ConversionResult<Void> customXsdValidation(@NotNull File schemaFile, @NotNull InputStream xmlToValidate) throws EigorException;

    /**
     * Return all the supported invoice source formats.
     * <p>
     * If a value is contained in this group, it means it can be
     * used as a source format to convert or check the invoice.
     * </p>
     */
    Set<String> supportedSourceFormats();

    /**
     * Return all the supported invoice target formats.
     * <p>
     * If a value is contained in this group, it means it can be
     * used as a target format in a conversion.
     * </p>
     */
    Set<String> supportedTargetFormats();

    /**
     * Return the detailed description of the current version.
     */
    String getDetailedVersion();

    /**
     * Return the brief description of the current version.
     */
    String getVersion();
}
