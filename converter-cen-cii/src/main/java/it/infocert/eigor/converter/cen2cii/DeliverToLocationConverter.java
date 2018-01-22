package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Deliver To Location Converter
 */
public class DeliverToLocationConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0013DeliveryInformation().isEmpty()) {
            BG0013DeliveryInformation bg0013 = cenInvoice.getBG0013DeliveryInformation(0);
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace rsmNs = rootElement.getNamespace("rsm");
            Namespace ramNs = rootElement.getNamespace("ram");
            
            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeDelivery = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeDelivery = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeDelivery");
            if (applicableHeaderTradeDelivery == null) {
                applicableHeaderTradeDelivery = new Element("ApplicableHeaderTradeDelivery", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeDelivery);
            }

            Element shipToTradeParty = findNamespaceChild(applicableHeaderTradeDelivery, namespacesInScope, "ShipToTradeParty");
            if (shipToTradeParty == null) {
                shipToTradeParty = new Element("ShipToTradeParty", ramNs);
                applicableHeaderTradeDelivery.addContent(shipToTradeParty);
            }

            if (!bg0013.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0071 = bg0013.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs); //maybe GlobalID?
                id.setText(bt0071.getIdentifier());
                if (bt0071.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", bt0071.getIdentificationSchema());
                }
                shipToTradeParty.addContent(id);
            }
            
            if(!bg0013.getBG0015DeliverToAddress().isEmpty()){
                BG0015DeliverToAddress bg0015 = bg0013.getBG0015DeliverToAddress(0);

                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                shipToTradeParty.addContent(postalTradeAddress);

                if(!bg0015.getBT0075DeliverToAddressLine1().isEmpty()){
                    BT0075DeliverToAddressLine1 bt0075 = bg0015.getBT0075DeliverToAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0075.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if(!bg0015.getBT0076DeliverToAddressLine2().isEmpty()){
                    BT0076DeliverToAddressLine2 bt0076 = bg0015.getBT0076DeliverToAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0076.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if(!bg0015.getBT0165DeliverToAddressLine3().isEmpty()){
                    BT0165DeliverToAddressLine3 bt0165 = bg0015.getBT0165DeliverToAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0165.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if(!bg0015.getBT0077DeliverToCity().isEmpty()){
                    BT0077DeliverToCity bt0077 = bg0015.getBT0077DeliverToCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0077.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if(!bg0015.getBT0078DeliverToPostCode().isEmpty()){
                    BT0078DeliverToPostCode bt0078 = bg0015.getBT0078DeliverToPostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0078.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if(!bg0015.getBT0079DeliverToCountrySubdivision().isEmpty()){
                    BT0079DeliverToCountrySubdivision bt0079 = bg0015.getBT0079DeliverToCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0079.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }

                if(!bg0015.getBT0080DeliverToCountryCode().isEmpty()){
                    BT0080DeliverToCountryCode bt0080 = bg0015.getBT0080DeliverToCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0080.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }
            }
        }
    }
}
