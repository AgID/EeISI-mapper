package it.infocert.eigor.converter.cii2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.api.xml.FilesystemXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The CII to CEN format converter
 */
@SuppressWarnings("unchecked")
public class Cii2Cen extends AbstractToCenConverter {

	private static final Logger log = LoggerFactory.getLogger(Cii2Cen.class);
	private static final String FORMAT = "cii";
	private final DefaultResourceLoader drl = new DefaultResourceLoader();
	private final EigorConfiguration configuration;
	private static final ConversionRegistry conversionRegistry = new ConversionRegistry(

			// enums
			 CountryNameToIso31661CountryCodeConverter.newConverter(),
			 LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),

             StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
             LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),

             StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
             LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),

			 Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
			 Iso31661CountryCodesToStringConverter.newConverter(),
			 UnitOfMeasureCodesToStringConverter.newConverter(),

			 StringToUntdid4461PaymentMeansCode.newConverter(),

            StringToUntdid2475PaymentTimeReference.newConverter(),

            // date
             StringToJavaLocalDateConverter.newConverter("yyyyMMdd"),

			// numbers
			 StringToBigDecimalConverter.newConverter(),

			// string
             StringToStringConverter.newConverter()
			);

	private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cii-cen.mapping.one-to-many";
	private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cii-cen.mapping.one-to-one";
	private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cii-cen.mapping.many-to-one";
	private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cii-cen.mapping.custom";

	private XSDValidator xsdValidator;
	private SchematronValidator schematronValidator;
    private SchematronValidator ciusValidator;

	public Cii2Cen(IReflections reflections, EigorConfiguration configuration) {
		super(reflections, conversionRegistry, configuration, ErrorCode.Location.CII_IN);
		this.configuration = checkNotNull(configuration);
	}

	@Override
	public void configure() throws ConfigurationException {
		super.configure();

		// load the XSD.
		{
			String mandatoryString = this.configuration.getMandatoryString("eigor.converter.cii-cen.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);
                xsdValidator = new FilesystemXSDValidator(xsdFile.getFile(), ErrorCode.Location.CII_IN);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for CII2CEN from '" + mandatoryString + "'.", e);
            }
		}

		// load the CII schematron validator.
        try {
            Resource ciiSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.cii-cen.schematron") );
			boolean schematronAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.cii-cen.schematron.auto-update-xslt"));
			schematronValidator = new SchematronValidator(ciiSchemaFile.getFile(), true, schematronAutoUpdate, ErrorCode.Location.CII_IN);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        // load the CII schematron validator.
        try {
            Resource ciusSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.cii-cen.cius") );
			boolean ciusAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.cii-cen.cius.auto-update-xslt"));
			ciusValidator = new SchematronValidator(ciusSchemaFile.getFile(), true, ciusAutoUpdate, ErrorCode.Location.CII_IN);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        configurableSupport.configure();
	}

	@Override
	public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream)
			throws SyntaxErrorInInvoiceFormatException {

		List<IConversionIssue> errors = new ArrayList<>();

		InputStream clonedInputStream = null;

		try {
			byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
			clonedInputStream = new ByteArrayInputStream(bytes);

			List<IConversionIssue> xsdValidationErrors = xsdValidator.validate(bytes);
			if(xsdValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_XSD_VALIDATION);
			}
			errors.addAll(xsdValidationErrors);

			List<IConversionIssue> schematronValidationErrors = schematronValidator.validate(bytes);
			if(schematronValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_SCHEMATRON_VALIDATION);
			}
			errors.addAll(schematronValidationErrors);

			List<IConversionIssue> ciusValidationErrors = ciusValidator.validate(bytes);
			if(ciusValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_CIUS_VALIDATION);
            }
			errors.addAll(ciusValidationErrors);

		} catch (IOException e) {
			errors.add(ConversionIssue.newWarning(e,"Error during validation", ErrorCode.Location.CII_IN, ErrorCode.Action.GENERIC, ErrorCode.Error.ILLEGAL_VALUE, Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));

		}

		Document document;
		ConversionResult<BG0000Invoice> result;
		try {
			document = getDocument(clonedInputStream);

			result = applyOne2OneTransformationsBasedOnMapping(document, errors);
			result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, errors);
            applyCustomMapping(result.getResult(), document, errors);
		} catch (RuntimeException e) {
			throw new EigorRuntimeException(e.getMessage(), ErrorCode.Location.CII_IN, ErrorCode.Action.CONFIGURED_MAP, ErrorCode.Error.INVALID, e);
		}
		return result;
	}

	private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> mappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> mapping : mappings) {
            mapping.map(invoice, document, errors, ErrorCode.Location.CII_IN,  this.configuration);
        }
    }

	@Override
	public boolean support(String format) {
		if(format == null){
			log.error(IConstants.NULL_FORMAT);
			return false;
		}
		return FORMAT.equals(format.toLowerCase().trim());
	}

	@Override
	public Set<String> getSupportedFormats() {
		return new HashSet<>(Arrays.asList(FORMAT));
	}

	@Override
	public String getMappingRegex() {
		return "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?";
	}

	@Override
	protected String getOne2OneMappingPath() {
		return configuration.getMandatoryString(ONE2ONE_MAPPING_PATH);
	}

	@Override
	protected String getMany2OneMappingPath() {
		return configuration.getMandatoryString(MANY2ONE_MAPPING_PATH);
	}

	@Override
	protected String getOne2ManyMappingPath() { return configuration.getMandatoryString(ONE2MANY_MAPPING_PATH);}

	@Override
	protected String getCustomMappingPath() {
		return CUSTOM_CONVERTER_MAPPING_PATH;
	}

	@Override public String getName() {
		return "converter-cii-cen";
	}

}
