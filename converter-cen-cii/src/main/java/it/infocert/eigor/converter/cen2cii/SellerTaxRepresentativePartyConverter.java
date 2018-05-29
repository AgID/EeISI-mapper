package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

public class SellerTaxRepresentativePartyConverter extends CustomConverterUtils implements CustomMapping<Document> {
    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0011SellerTaxRepresentativeParty().isEmpty()) {
            final BG0011SellerTaxRepresentativeParty bg0011 = invoice.getBG0011SellerTaxRepresentativeParty(0);
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace ramNs = rootElement.getNamespace("ram");
            Namespace rsmNs = rootElement.getNamespace("rsm");

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeAgreement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }

            final Element sellerTaxRepresentativeTradeParty = new Element("SellerTaxRepresentativeTradeParty", ramNs);
            applicableHeaderTradeAgreement.addContent(sellerTaxRepresentativeTradeParty);


            if (!bg0011.getBT0062SellerTaxRepresentativeName().isEmpty()) {
                BT0062SellerTaxRepresentativeName bt0062 = bg0011.getBT0062SellerTaxRepresentativeName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0062.getValue());
                sellerTaxRepresentativeTradeParty.addContent(name);
            }

            if (!bg0011.getBT0063SellerTaxRepresentativeVatIdentifier().isEmpty()) {
                Identifier bt0063 = bg0011.getBT0063SellerTaxRepresentativeVatIdentifier(0).getValue();
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                Element id = new Element("ID", ramNs);
                String schema = bt0063.getIdentificationSchema();
                String identifier = bt0063.getIdentifier();
                if (schema != null) {
                    id.setText(String.format("%s %s", schema, identifier));
                } else {
                    id.setText(identifier);
                }
                id.setAttribute("schemeID", "VA");
                specifiedTaxRegistration.addContent(id);
                sellerTaxRepresentativeTradeParty.addContent(specifiedTaxRegistration);
            }

            if (!bg0011.getBG0012SellerTaxRepresentativePostalAddress().isEmpty()) {
                BG0012SellerTaxRepresentativePostalAddress bg0012 = bg0011.getBG0012SellerTaxRepresentativePostalAddress(0);

                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                sellerTaxRepresentativeTradeParty.addContent(postalTradeAddress);

                if (!bg0012.getBT0064TaxRepresentativeAddressLine1().isEmpty()) {
                    BT0064TaxRepresentativeAddressLine1 bt0064 = bg0012.getBT0064TaxRepresentativeAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0064.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if (!bg0012.getBT0065TaxRepresentativeAddressLine2().isEmpty()) {
                    BT0065TaxRepresentativeAddressLine2 bt0065 = bg0012.getBT0065TaxRepresentativeAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0065.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if (!bg0012.getBT0164TaxRepresentativeAddressLine3().isEmpty()) {
                    BT0164TaxRepresentativeAddressLine3 bt0164 = bg0012.getBT0164TaxRepresentativeAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0164.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if (!bg0012.getBT0066TaxRepresentativeCity().isEmpty()) {
                    BT0066TaxRepresentativeCity bt0066 = bg0012.getBT0066TaxRepresentativeCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0066.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if (!bg0012.getBT0067TaxRepresentativePostCode().isEmpty()) {
                    BT0067TaxRepresentativePostCode bt0067 = bg0012.getBT0067TaxRepresentativePostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0067.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if (!bg0012.getBT0068TaxRepresentativeCountrySubdivision().isEmpty()) {
                    BT0068TaxRepresentativeCountrySubdivision bt0068 = bg0012.getBT0068TaxRepresentativeCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0068.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }

                if (!bg0012.getBT0069TaxRepresentativeCountryCode().isEmpty()) {
                    BT0069TaxRepresentativeCountryCode bt0069 = bg0012.getBT0069TaxRepresentativeCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0069.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }
            }
        }
    }
}
