package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0016PaymentInstructions;
import it.infocert.eigor.model.core.model.BT0089MandateReferenceIdentifier;

public class PaymentMeansConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub

		final Element root  = document.getRootElement();
		BT0089MandateReferenceIdentifier bt0089;
		Element elmentPaymentMandate;
		BG0016PaymentInstructions paymentMandate;
		Element ID = new Element("ID");
		Element paymentMeans;

		if(!cenInvoice.getBG0016PaymentInstructions().isEmpty())
			paymentMandate = cenInvoice.getBG0016PaymentInstructions(0);
		else return;
		
		if(!paymentMandate.getBG0019DirectDebit().isEmpty()) {
			if(paymentMandate.getBG0019DirectDebit(0).getBT0089MandateReferenceIdentifier().isEmpty()) {
				ID.setText(paymentMandate.getBG0019DirectDebit(0).getBT0089MandateReferenceIdentifier(0).getValue());
		}
		}
	}

}