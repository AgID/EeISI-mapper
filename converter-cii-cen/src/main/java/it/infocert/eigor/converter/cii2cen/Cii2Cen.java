package it.infocert.eigor.converter.cii2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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

            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),

            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),

            new StringToJavaLocalDateConverter("yyyyMMdd"),

			// string
            new StringToStringConverter()
			);

	public static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cii-cen.mapping.one-to-one";

	private XSDValidator xsdValidator;
	private SchematronValidator schematronValidator;


	public Cii2Cen(Reflections reflections, EigorConfiguration configuration) {
		super(reflections, conversionRegistry, configuration);
		setMappingRegex("(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?");
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
                xsdValidator = new XSDValidator(xsdFile.getFile());
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for CII2CEN from '" + mandatoryString + "'.", e);
            }
		}

		// load the CII schematron validator.
        try {
            Resource ciiSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.cii-cen.schematron") );
            schematronValidator = new SchematronValidator(ciiSchemaFile.getFile(), true);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        configurableSupport.configure();
	}

	@Override
	public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream)
			throws SyntaxErrorInInvoiceFormatException {
		
		List<ConversionIssue> errors = new ArrayList<>();

		InputStream clonedInputStream = null;

		try {
			byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
			clonedInputStream = new ByteArrayInputStream(bytes);

			List<ConversionIssue> xsdValidationErrors = xsdValidator.validate(bytes);
			if(xsdValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_XSD_VALIDATION);
			}
			errors.addAll(xsdValidationErrors);

			List<ConversionIssue> schematronValidationErrors = schematronValidator.validate(bytes);
			if(schematronValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_SCHEMATRON_VALIDATION);
			}
			errors.addAll(schematronValidationErrors);

		} catch (IOException e) {
			e.printStackTrace();
		}

		Document document = null;
		ConversionResult<BG0000Invoice> result;
		try {
			document = getDocument(clonedInputStream);
			BG0000Invoice invoice = new BG0000Invoice();
			result = new ConversionResult<>(errors, invoice);
			result = applyOne2OneTransformationsBasedOnMapping(document, errors);
		} catch (JDOMException | IOException e) {
			throw new RuntimeException(e);
		}
		return result;
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
	protected String getOne2OneMappingPath() {
		return configuration.getMandatoryString(ONE2ONE_MAPPING_PATH);
	}

	@Override public String getName() {
		return "cii-cen";
	}

}
