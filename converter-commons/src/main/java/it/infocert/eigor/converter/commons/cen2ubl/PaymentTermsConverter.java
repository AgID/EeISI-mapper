package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class PaymentTermsConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document xmlInvoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Element invoice = xmlInvoice.getRootElement();
        int indexOfAccountingCustomerParty = invoice.indexOf(invoice.getChild("AccountingCustomerParty"));

        if(!cenInvoice.getBT0020PaymentTerms().isEmpty()){

            Element note = new Element("Note");
            note.setText( cenInvoice.getBT0020PaymentTerms().get(0).getValue() );

            Element payementTerms = new Element("PaymentTerms");
            payementTerms.addContent( note );
            invoice.addContent(indexOfAccountingCustomerParty+1, payementTerms);

        }


    }

}
