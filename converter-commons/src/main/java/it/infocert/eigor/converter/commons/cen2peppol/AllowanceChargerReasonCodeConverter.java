package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;

public class AllowanceChargerReasonCodeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub

		Element root = document.getRootElement();
		Element allowanceCharge = new Element("AllowanceCharge");
		BG0020DocumentLevelAllowances invoiceAllowance1;
		Element allowanceReasonCode = new Element("AllowanceChargeReasonCode");
		Element invoiceLine;
		Untdid5189ChargeAllowanceDescriptionCodes allowanceCode = null;
		BG0027InvoiceLineAllowances invoiceAllowance2;



		if(!cenInvoice.getBG0020DocumentLevelAllowances().isEmpty()) {
			invoiceAllowance1 = cenInvoice.getBG0020DocumentLevelAllowances(0);
			if(!invoiceAllowance1.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {	
				allowanceCode = setDefaultAllowanceChargerCode(invoiceAllowance1.getBT0098DocumentLevelAllowanceReasonCode(0).getValue());
				if(root.getChild("AllowanceCharge") == null) {
					allowanceReasonCode.setText(allowanceCode.toString());
					allowanceCharge.addContent(allowanceReasonCode);
					root.addContent(allowanceCharge);
				}
			}
		}
		else return;

		if(!cenInvoice.getBG0025InvoiceLine().isEmpty())
			if(!cenInvoice.getBG0025InvoiceLine().isEmpty() && 
					!cenInvoice.getBG0025InvoiceLine(0).getBG0027InvoiceLineAllowances().isEmpty()){

				invoiceAllowance2 = cenInvoice.getBG0025InvoiceLine(0).getBG0027InvoiceLineAllowances(0);
				allowanceCode = setDefaultAllowanceChargerCode(invoiceAllowance2.getBT0140InvoiceLineAllowanceReasonCode(0).getValue());
				if(root.getChild("InvoiceLine") == null) {
					invoiceLine = new Element("InvoiceLine");
					allowanceReasonCode.setText(allowanceCode.toString());
					allowanceCharge.addContent(allowanceReasonCode);
					invoiceLine.addContent(allowanceCharge);
					root.addContent(invoiceLine);

				}

			}
			else return;

	}

	public Untdid5189ChargeAllowanceDescriptionCodes setDefaultAllowanceChargerCode(Untdid5189ChargeAllowanceDescriptionCodes val) {

		Untdid5189ChargeAllowanceDescriptionCodes codeValue;

		switch(val)
		{
		case Code41:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code41;
		case Code42:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code42;
		case Code60:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code60;
		case Code62:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code62;
		case Code63:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code63;
		case Code64:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code64;
		case Code65:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code65;
		case Code66:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code66;
		case Code67:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code67;
		case Code68:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code68;
		case Code70:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code70;
		case Code71:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code72;
		case Code88:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code88;
		case Code95:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		case Code100:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code100;
		case Code102:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code102;
		case Code103:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code103;
		case Code104:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code104;
		case Code105:  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code105;
		default: codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		}

		return codeValue;
	}

}