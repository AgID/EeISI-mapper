package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;


import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.converter.commons.cen2ubl.FirstLevelElementsConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class TaxAndDocumentCurrenceyCodeConverter extends FirstLevelElementsConverter{
	 
    @Override
    public void customMap(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

		// TODO Auto-generated method stub
    	
			if(!cenInvoice.getBT0006VatAccountingCurrencyCode().isEmpty() && !cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
				if(cenInvoice.getBT0006VatAccountingCurrencyCode(0).getValue().getCode().toString().equals(
						cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue().getCode())){
					  String converted = conversionRegistry.convert(Iso4217CurrenciesFundsCodes.class, String.class, cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue());
			          convert("DocumentCurrencyCode", converted);
				}
				
			}
			else {
				  String converted = conversionRegistry.convert(Iso4217CurrenciesFundsCodes.class, String.class, cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue());
		          convert("DocumentCurrencyCode", converted);
				  
			}

		

	}
}
