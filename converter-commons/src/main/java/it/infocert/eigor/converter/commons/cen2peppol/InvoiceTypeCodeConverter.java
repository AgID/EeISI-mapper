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
import net.sf.saxon.expr.instruct.ValueOf;

public class InvoiceTypeCodeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {

		Element root = document.getRootElement();
		Element invoiceTypeCode = null;
		
//		if (root != null) {
			if(root.getChild("InvoiceTypeCode") != null) { 

				invoiceTypeCode = root.getChild("InvoiceTypeCode");
			}
			else
				invoiceTypeCode = new Element("InvoiceTypeCode");
		
		String allowanceCode;

		allowanceCode = setDefaultAllowanceChargerCode(String.valueOf(cenInvoice.getBT0003InvoiceTypeCode(0).getValue().getCode()));
//        String value = String.valueOf(allowanceCode);
		invoiceTypeCode.addContent(allowanceCode);
		root.addContent(invoiceTypeCode);
	}


	public String setDefaultAllowanceChargerCode(String val) {

		String codeValue = "";
		String value = val;

		switch(value)
		{
		case "130":  codeValue = "380"; break;
		case "202":  codeValue = "380";
		case "203":  codeValue = "380";
		case "204":  codeValue = "380";
		case "211":  codeValue = "380";
		case "261":  codeValue = "380";
		case "295":  codeValue = "380";
		case "325":  codeValue = "380";
		case "326":  codeValue = "380";
		case "384":  codeValue = "380";
		case "387":  codeValue = "380";
		case "388":  codeValue = "380";
		case "389":  codeValue = "380";
		case "390":  codeValue = "380";
		case "394":  codeValue = "380";
		case "395":  codeValue = "380";
		case "456":  codeValue = "380";
		case "457":  codeValue = "380";
		case "633":  codeValue = "380";
		case "751":  codeValue = "380";
		case "935":  codeValue = "380";
		default: codeValue = value;
		}

		return codeValue;
	}

}
