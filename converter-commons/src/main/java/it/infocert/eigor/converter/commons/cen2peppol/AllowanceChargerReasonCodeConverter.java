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
					String value = String.valueOf(allowanceCode.getCode());
					allowanceReasonCode.setText(value);
					allowanceCharge.addContent(allowanceReasonCode);
					invoiceLine.addContent(allowanceCharge);
					root.addContent(invoiceLine);

				}

			}
			else return;
		
			

	}

	public Untdid5189ChargeAllowanceDescriptionCodes setDefaultAllowanceChargerCode(Untdid5189ChargeAllowanceDescriptionCodes val) {

		Untdid5189ChargeAllowanceDescriptionCodes codeValue;

		switch(val.toString())
		{
		case "41":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code41;
		case "42":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code42;
		case "60":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code60;
		case "62":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code62;
		case "63":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code63;
		case "64":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code64;
		case "65":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code65;
		case "66":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code66;
		case "67":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code67;
		case "68":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code68;
		case "70":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code70;
		case "71":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code72;
		case "88":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code88;
		case "95":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		case "100":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code100;
		case "102":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code102;
		case "103":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code103;
		case "104":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code104;
		case "105":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code105;
		default: codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		}

		return codeValue;
	}

}