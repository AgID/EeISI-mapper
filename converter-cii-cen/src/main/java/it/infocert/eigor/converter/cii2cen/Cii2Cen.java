package it.infocert.eigor.converter.cii2cen;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * The CII to CEN format converter
 */
@SuppressWarnings("unchecked")
public class Cii2Cen extends Abstract2CenConverter{
	
	public static final String FORMAT = "cii";
 
	public Cii2Cen(Reflections reflections, ConversionRegistry conversionRegistry) {
		super(reflections, conversionRegistry);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream)
			throws SyntaxErrorInInvoiceFormatException {
		
		BG0000Invoice x = new BG0000Invoice();
		ConversionResult<BG0000Invoice> result = new ConversionResult<>(x);
		return result;
	}

	@Override
	public boolean support(String format) {
		return FORMAT.equals(format.toLowerCase().trim());
	}

	@Override
	public Set<String> getSupportedFormats() {
		return new HashSet<>(Arrays.asList(FORMAT));
	}
	
	
	
}
