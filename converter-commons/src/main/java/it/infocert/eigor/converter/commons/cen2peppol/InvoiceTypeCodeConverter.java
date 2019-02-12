package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class InvoiceTypeCodeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {

		Element root = document.getRootElement();
		Element invoiceType = root.getChild("InvoiceTypeCode");
		Untdid1001InvoiceTypeCode allowanceCode;
		allowanceCode = setDefaultAllowanceChargerCode(cenInvoice.getBT0003InvoiceTypeCode(0).getValue());
		invoiceType.addContent(invoiceType);
		root.addContent(invoiceType);
	}


	public Untdid1001InvoiceTypeCode setDefaultAllowanceChargerCode(Untdid1001InvoiceTypeCode val) {

		Untdid1001InvoiceTypeCode codeValue = Untdid1001InvoiceTypeCode.Code380;

		switch(val)
		{
		case Code130:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code202:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code203:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code204:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code211:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code261:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code295:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code325:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code326:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code384:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code387:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code388:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code389:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code390:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code394:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code395:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code456:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code457:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code633:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code751:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case Code935:  codeValue = Untdid1001InvoiceTypeCode.Code380;
		default: codeValue = val;
		}

		return codeValue;
	}

}
