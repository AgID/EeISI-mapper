package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BT0033SellerAdditionalLegalInformation;

public class InvoiceTypeCodeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {

		Element root = document.getRootElement();
		Element invoiceType = root.getChild("InvoiceTypeCode");
		BT0003InvoiceTypeCode bt03;
		Untdid1001InvoiceTypeCode allowanceCode = null;
		
		if(!cenInvoice.getBT0003InvoiceTypeCode().isEmpty())
			bt03 = cenInvoice.getBT0003InvoiceTypeCode(0);
		
			allowanceCode = setDefaultAllowanceChargerCode(cenInvoice.getBT0003InvoiceTypeCode(0).getValue());
			invoiceType.addContent(invoiceType);
			root.addContent(invoiceType);
	}


	public Untdid1001InvoiceTypeCode setDefaultAllowanceChargerCode(Untdid1001InvoiceTypeCode val) {

		Untdid1001InvoiceTypeCode codeValue;

		switch(val)
		{
		case Code41:  codeValue = Untdid1001InvoiceTypeCode.Code141;
		case Code42:  codeValue = Untdid1001InvoiceTypeCode.Code42;
		case Code60:  codeValue = Untdid1001InvoiceTypeCode.Code60;
		case Code62:  codeValue = Untdid1001InvoiceTypeCode.Code62;
		case Code63:  codeValue = Untdid1001InvoiceTypeCode.Code63;
		case Code64:  codeValue = Untdid1001InvoiceTypeCode.Code64;
		case Code65:  codeValue = Untdid1001InvoiceTypeCode.Code65;
		case Code66:  codeValue = Untdid1001InvoiceTypeCode.Code66;
		case Code67:  codeValue = Untdid1001InvoiceTypeCode.Code67;
		case Code68:  codeValue = Untdid1001InvoiceTypeCode.Code68;
		case Code70:  codeValue = Untdid1001InvoiceTypeCode.Code70;
		case Code71:  codeValue = Untdid1001InvoiceTypeCode.Code72;
		case Code88:  codeValue = Untdid1001InvoiceTypeCode.Code88;
		case Code95:  codeValue = Untdid1001InvoiceTypeCode.Code95;
		case Code100:  codeValue = Untdid1001InvoiceTypeCode.Code100;
		case Code102:  codeValue = Untdid1001InvoiceTypeCode.Code102;
		case Code103:  codeValue = Untdid1001InvoiceTypeCode.Code103;
		case Code104:  codeValue = Untdid1001InvoiceTypeCode.Code104;
		case Code105:  codeValue = Untdid1001InvoiceTypeCode.Code105;
		default: codeValue = Untdid1001InvoiceTypeCode.Code95;
		}

		return codeValue;
	}

}
