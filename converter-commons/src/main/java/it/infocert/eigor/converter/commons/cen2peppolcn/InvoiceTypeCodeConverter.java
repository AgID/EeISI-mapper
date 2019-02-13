package it.infocert.eigor.converter.commons.cen2peppolcn;

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
		Untdid1001InvoiceTypeCode codeValue;
		switch(val)
		{
		case Code262:  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case Code296:  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case Code308:  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case Code420:  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case Code458:  codeValue = Untdid1001InvoiceTypeCode.Code381;
		default: codeValue = val;
		}

		return codeValue;
	}

}
