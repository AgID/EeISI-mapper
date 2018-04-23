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

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller bg0004 = invoice.getBG0004Seller(0);
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

            Element sellerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SellerTradeParty");
            if (sellerTradeParty == null) {
                sellerTradeParty = new Element("SellerTradeParty", ramNs);
                applicableHeaderTradeAgreement.addContent(sellerTradeParty);
            }

            if (!invoice.getBG0004Seller().isEmpty()) {
                if (!bg0004.getBT0029SellerIdentifierAndSchemeIdentifier().isEmpty()) {
                    for (BT0029SellerIdentifierAndSchemeIdentifier bt0029 : bg0004.getBT0029SellerIdentifierAndSchemeIdentifier()) {
                        Identifier identifier = bt0029.getValue();
                        Element id = new Element("ID", ramNs);
                        id.setText(identifier.getIdentifier());
                        if (identifier.getIdentificationSchema() != null) {
                            id.setAttribute("schemeID", identifier.getIdentificationSchema());
                        }
                        sellerTradeParty.addContent(id);
                    }

                }

                if (!bg0004.getBT0027SellerName().isEmpty()) {
                    BT0027SellerName bt0027 = bg0004.getBT0027SellerName(0);
                    Element name = new Element("Name", ramNs);
                    name.setText(bt0027.getValue());
                    sellerTradeParty.addContent(name);
                }

                if (!bg0004.getBT0033SellerAdditionalLegalInformation().isEmpty()) {
                    BT0033SellerAdditionalLegalInformation bt0033 = bg0004.getBT0033SellerAdditionalLegalInformation(0);
                    Element description = new Element("Description", ramNs);
                    description.setText(bt0033.getValue());
                    sellerTradeParty.addContent(description);
                }

                Element specifiedLegalOrganization = new Element("SpecifiedLegalOrganization", ramNs);

                if (!bg0004.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                    Identifier bt0030 = bg0004.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
                    Element id = new Element("ID", ramNs);
                    id.setText(bt0030.getIdentifier());
                    if (bt0030.getIdentificationSchema() != null) {
                        id.setAttribute("schemeID", bt0030.getIdentificationSchema());
                    }
                    specifiedLegalOrganization.addContent(id);
                }

                if (!bg0004.getBT0028SellerTradingName().isEmpty()) {
                    BT0028SellerTradingName bt0028 = bg0004.getBT0028SellerTradingName(0);
                    Element tradingBusinessName = new Element("TradingBusinessName", ramNs);
                    tradingBusinessName.setText(bt0028.getValue());
                    specifiedLegalOrganization.addContent(tradingBusinessName);
                }

                if (!specifiedLegalOrganization.getChildren().isEmpty()) {
                    sellerTradeParty.addContent(specifiedLegalOrganization);
                }


                if (!bg0004.getBG0006SellerContact().isEmpty()) {
                    BG0006SellerContact bg0006 = bg0004.getBG0006SellerContact(0);
                    Element definedTradeContact = new Element("DefinedTradeContact", ramNs);
                    sellerTradeParty.addContent(definedTradeContact);

                    if (!bg0006.getBT0041SellerContactPoint().isEmpty()) {
                        BT0041SellerContactPoint bt0041 = bg0006.getBT0041SellerContactPoint(0);
                        Element personName = new Element("PersonName", ramNs);
                        personName.setText(bt0041.getValue());
                        definedTradeContact.addContent(personName);
                    }

                    if (!bg0006.getBT0042SellerContactTelephoneNumber().isEmpty()) {
                        BT0042SellerContactTelephoneNumber bt0042 = bg0006.getBT0042SellerContactTelephoneNumber(0);
                        Element telephoneUniversalCommunication = new Element("TelephoneUniversalCommunication", ramNs);
                        Element completeNumber = new Element("CompleteNumber", ramNs);
                        completeNumber.setText(bt0042.getValue());
                        telephoneUniversalCommunication.addContent(completeNumber);
                        definedTradeContact.addContent(telephoneUniversalCommunication);
                    }

                    if (!bg0006.getBT0043SellerContactEmailAddress().isEmpty()) {
                        BT0043SellerContactEmailAddress bt0043 = bg0006.getBT0043SellerContactEmailAddress(0);
                        Element emailURIUniversalCommunication = new Element("EmailURIUniversalCommunication", ramNs);
                        Element uriid = new Element("URIID", ramNs);
                        uriid.setText(bt0043.getValue());
                        emailURIUniversalCommunication.addContent(uriid);
                        definedTradeContact.addContent(emailURIUniversalCommunication);
                    }
                }

                if (!bg0004.getBG0005SellerPostalAddress().isEmpty()) {
                    BG0005SellerPostalAddress bg0005 = bg0004.getBG0005SellerPostalAddress(0);
                    Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                    sellerTradeParty.addContent(postalTradeAddress);

                    if (!bg0005.getBT0038SellerPostCode().isEmpty()) {
                        BT0038SellerPostCode bt0038 = bg0005.getBT0038SellerPostCode(0);
                        Element postcodeCode = new Element("PostcodeCode", ramNs);
                        postcodeCode.setText(bt0038.getValue());
                        postalTradeAddress.addContent(postcodeCode);
                    }

                    if (!bg0005.getBT0035SellerAddressLine1().isEmpty()) {
                        BT0035SellerAddressLine1 bt0035 = bg0005.getBT0035SellerAddressLine1(0);
                        Element lineOne = new Element("LineOne", ramNs);
                        lineOne.setText(bt0035.getValue());
                        postalTradeAddress.addContent(lineOne);
                    }

                    if (!bg0005.getBT0036SellerAddressLine2().isEmpty()) {
                        BT0036SellerAddressLine2 bt0036 = bg0005.getBT0036SellerAddressLine2(0);
                        Element lineTwo = new Element("LineTwo", ramNs);
                        lineTwo.setText(bt0036.getValue());
                        postalTradeAddress.addContent(lineTwo);
                    }

                    if (!bg0005.getBT0162SellerAddressLine3().isEmpty()) {
                        BT0162SellerAddressLine3 bt0162 = bg0005.getBT0162SellerAddressLine3(0);
                        Element lineThree = new Element("LineThree", ramNs);
                        lineThree.setText(bt0162.getValue());
                        postalTradeAddress.addContent(lineThree);
                    }

                    if (!bg0005.getBT0037SellerCity().isEmpty()) {
                        BT0037SellerCity bt0037 = bg0005.getBT0037SellerCity(0);
                        Element cityName = new Element("CityName", ramNs);
                        cityName.setText(bt0037.getValue());
                        postalTradeAddress.addContent(cityName);
                    }

                    if (!bg0005.getBT0040SellerCountryCode().isEmpty()) {
                        BT0040SellerCountryCode bt0040 = bg0005.getBT0040SellerCountryCode(0);
                        Element countryID = new Element("CountryID", ramNs);
                        countryID.setText(bt0040.getValue().getIso2charCode());
                        postalTradeAddress.addContent(countryID);
                    }

                    if (!bg0005.getBT0039SellerCountrySubdivision().isEmpty()) {
                        BT0039SellerCountrySubdivision bt0039 = bg0005.getBT0039SellerCountrySubdivision(0);
                        Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                        countrySubDivisionName.setText(bt0039.getValue());
                        postalTradeAddress.addContent(countrySubDivisionName);
                    }
                }


                if (!bg0004.getBT0034SellerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                    Identifier bt0034 = bg0004.getBT0034SellerElectronicAddressAndSchemeIdentifier(0).getValue();
                    Element uriUniversalCommunication = new Element("URIUniversalCommunication", ramNs);
                    Element uriid = new Element("URIID", ramNs);
                    uriid.setText(bt0034.getIdentifier());
                    if (bt0034.getIdentificationSchema() != null) {
                        uriid.setAttribute("schemeID", bt0034.getIdentificationSchema());
                    }
                    uriUniversalCommunication.addContent(uriid);
                    sellerTradeParty.addContent(uriUniversalCommunication);
                }

                for (BT0031SellerVatIdentifier bt0031 : bg0004.getBT0031SellerVatIdentifier()) {
                    Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                    Element id = new Element("ID", ramNs);
                    id.setText(bt0031.getValue());
                    id.setAttribute("schemeID", "VA");
                    specifiedTaxRegistration.addContent(id);
                    sellerTradeParty.addContent(specifiedTaxRegistration);
                }

                for (BT0032SellerTaxRegistrationIdentifier bt0032 : bg0004.getBT0032SellerTaxRegistrationIdentifier()) {
                    Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                    Element id = new Element("ID", ramNs);
                    id.setText(bt0032.getValue());
                    id.setAttribute("schemeID", "FC");
                    specifiedTaxRegistration.addContent(id);
                    sellerTradeParty.addContent(specifiedTaxRegistration);
                }

            }

        }
    }
}