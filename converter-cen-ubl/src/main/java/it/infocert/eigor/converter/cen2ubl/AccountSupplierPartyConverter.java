package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class AccountSupplierPartyConverter implements CustomMapping<Document> {

    private Element supplier;
    private final String SUPPLIER = "AccountingSupplierParty";
    private final String PARTY = "Party";
    private Element party;
    private Element root;

    public AccountSupplierPartyConverter() {
        supplier = new Element(SUPPLIER);
        this.party = new Element(this.PARTY);
        supplier.addContent(party);
    }

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        root = document.getRootElement();
        this.supplier = root.getChild(SUPPLIER) == null ? root.addContent(supplier) : root.getChild(SUPPLIER);

        if (invoice.getBG0004Seller().isEmpty()) {
            return;
        }


        BG0004Seller seller = invoice.getBG0004Seller(0);

        seller


    }
}
