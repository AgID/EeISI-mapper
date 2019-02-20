package it.infocert.eigor.converter.commons.cen2peppolcn;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.converter.commons.cen2ubl.FirstLevelElementsConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class InvoiceTypeCodeConverter extends FirstLevelElementsConverter{

	@Override
	protected void customMap(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation) {

		Element root = document.getRootElement();
		Element invoiceTypeCode = null;
		
		if(root.getChild("InvoiceTypeCode") != null) { 

			invoiceTypeCode = root.getChild("InvoiceTypeCode");
		}
		else
			invoiceTypeCode = new Element("InvoiceTypeCode");
	
		Untdid1001InvoiceTypeCode allowanceCode = setDefaultAllowanceChargerCode(cenInvoice.getBT0003InvoiceTypeCode(0).getValue());
        String converted = conversionRegistry.convert(Untdid1001InvoiceTypeCode.class, String.class, allowanceCode);

        if (ErrorCode.Location.UBL_OUT.equals(callingLocation)) {
            convert("InvoiceTypeCode", converted);
        }
        if (ErrorCode.Location.UBLCN_OUT.equals(callingLocation)) {
            convert("CreditNoteTypeCode", converted);
        }


	}

	public Untdid1001InvoiceTypeCode setDefaultAllowanceChargerCode(Untdid1001InvoiceTypeCode val) {
		Untdid1001InvoiceTypeCode codeValue;
		switch(String.valueOf(val.getCode()))
		{
		case "262":  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case "296":  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case "308":  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case "420":  codeValue = Untdid1001InvoiceTypeCode.Code381;
		case "458":  codeValue = Untdid1001InvoiceTypeCode.Code381;
		default: codeValue = val;
		}

		return codeValue;
	}



}
