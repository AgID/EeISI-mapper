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
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import net.sf.saxon.expr.instruct.ValueOf;

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

        if (ErrorCode.Location.PEPPOL_OUT.equals(callingLocation)) {
            convert("InvoiceTypeCode", converted);
        }
        
	}


	public Untdid1001InvoiceTypeCode setDefaultAllowanceChargerCode(Untdid1001InvoiceTypeCode val) {

		Untdid1001InvoiceTypeCode codeValue;

		switch(String.valueOf(val.getCode()))
		{
		case "130":  codeValue = Untdid1001InvoiceTypeCode.Code380; break;
		case "202":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "203":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "204":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "211":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "261":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "295":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "325":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "326":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "384":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "387":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "388":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "389":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "390":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "394":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "395":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "456":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "457":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "633":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "751":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		case "935":  codeValue = Untdid1001InvoiceTypeCode.Code380;
		default: codeValue = val;
		}

		return codeValue;
	}

}
