package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.StringToIso4217CurrenciesFundsCodesConverter;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.conversion.StringToStringConverter;
import it.infocert.eigor.api.conversion.StringToUntdid1001InvoiceTypeCodeConverter;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InvoiceCenXpathMappingValidator;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.xml.sax.SAXException;

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
	private IXMLValidator ciiValidator;
 
	public Cii2Cen(Reflections reflections, EigorConfiguration configuration) {
		super(reflections, conversionRegistry, configuration);
		setMappingRegex("(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?");
		this.configuration = checkNotNull(configuration);
	}
	
	@Override
	public void configure() throws ConfigurationException {
		super.configure();
		
		// load the XSD
		{
			String mandatoryString = this.configuration.getMandatoryString("eigor.converter.cii-cen.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);
                xsdValidator = new XSDValidator(xsdFile.getFile());
//                System.out.println(xsdFile.getFile().getName());
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for CII2CEN from '" + mandatoryString + "'.", e);
            }
		}
		
		// load the CII schematron validator.
        try {
            Resource ciiSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.cii-cen.schematron") );
            ciiValidator = new SchematronValidator(ciiSchemaFile.getFile(), true);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }
        
        configurableSupport.configure();
	}

	@Override
	public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream)
			throws SyntaxErrorInInvoiceFormatException {
		
		List<ConversionIssue> errors = new ArrayList<>();
		
//		File ciiSchemaFile = new File("converterdata/converter-cii-cen/cii/schematron-xslt/EN16931-CII-validation.xslt");
//		File xsdFile = new File("converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd");
		
//        XSDValidator xsdValidator = null;
//        try {
//            xsdValidator = new XSDValidator(xsdFile);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        }
//		IXMLValidator ciiValidator = new SchematronValidator(ciiSchemaFile, true);
		
		InputStream clonedInputStream = null;
		
		try {
			byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
			clonedInputStream = new ByteArrayInputStream(bytes);
			
			List<ConversionIssue> xsdValidationErrors = xsdValidator.validate(bytes);
			if(xsdValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_XSD_VALIDATION);
			}
			errors.addAll(xsdValidationErrors);
			
			List<ConversionIssue> schematronValidationErrors = ciiValidator.validate(bytes);
			if(schematronValidationErrors.isEmpty()){
				log.info(IConstants.SUCCESS_SCHEMATRON_VALIDATION);
			}
			errors.addAll(schematronValidationErrors);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Document document = getDocument(clonedInputStream);
		BG0000Invoice invoice = new BG0000Invoice();
		ConversionResult<BG0000Invoice> result = new ConversionResult<>(errors, invoice);
		result = applyOne2OneTransformationsBasedOnMapping(document, errors);
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
