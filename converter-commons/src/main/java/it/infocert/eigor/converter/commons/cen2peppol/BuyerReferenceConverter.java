package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BT0034SellerElectronicAddressAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0049BuyerElectronicAddressAndSchemeIdentifier;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuyerReferenceConverter implements CustomMapping<Document> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final String CUSTOMER = "AccountingCustomerParty";
    private final String PARTY = "Party";
    private final String Endpoint = "EndpointID";

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        final Element root = document.getRootElement();
        final Element partyElm;
        final Element supplier = root.getChild(CUSTOMER);
        Element accountSupplierPartyElm = new Element(CUSTOMER);
        if (supplier == null) {

            partyElm = new Element(PARTY);
            accountSupplierPartyElm.addContent(partyElm);

        } else {
            partyElm = supplier.getChild(PARTY);
            accountSupplierPartyElm.addContent(partyElm);
        }

        BG0007Buyer buyer = invoice.getBG0007Buyer(0);

            String identifierText;
            String identificationSchemaStr;

            if (buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                identifierText = "NA";
                identificationSchemaStr = "0130";
            } else {
                BT0049BuyerElectronicAddressAndSchemeIdentifier bt49 = buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0);
                identifierText = bt49.getValue().getIdentifier();
                identificationSchemaStr = bt49.getValue().getIdentificationSchema();
            }

            Element endpointElm = new Element(Endpoint);
            endpointElm.setText(identifierText);
            endpointElm.setAttribute("schemeID",identificationSchemaStr);
            partyElm.addContent(endpointElm);
            root.addContent(accountSupplierPartyElm);
    }
}