package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;


import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class TaxAndDocumentCurrenceyCodeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub

		Element root = document.getRootElement();
		final Element taxCurrencyCode;
		final Element documentCurrencyCode;
		
		if(root!=null) {
			if(!cenInvoice.getBT0006VatAccountingCurrencyCode().isEmpty() && !cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
				if(cenInvoice.getBT0006VatAccountingCurrencyCode(0).getValue() == 
						cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue()){
					Iso4217CurrenciesFundsCodes currencyCode;
					currencyCode = cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue();
					documentCurrencyCode = new Element("DocumentCurrencyCode");
					documentCurrencyCode.setText(currencyCode.toString());
					root.addContent(documentCurrencyCode);
				}
		}
		else {
				taxCurrencyCode = new Element("TaxCurrencyCode");
				taxCurrencyCode.setText(cenInvoice.getBT0005InvoiceCurrencyCode(0).getValue().toString());
				documentCurrencyCode = new Element("DocumentCurrencyCode");
				documentCurrencyCode.setText(cenInvoice.getBT0006VatAccountingCurrencyCode(0).getValue().toString());
				root.addContent(taxCurrencyCode);
				root.addContent(documentCurrencyCode);
		}
	}

}
}
