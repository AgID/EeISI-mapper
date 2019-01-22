package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Buyer Custom Converter
 */
public class BuyerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
            rootElement.addContent(supplyChainTradeTransaction);
        }

        Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
        if (applicableHeaderTradeAgreement == null) {
            applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
        }

        if (!invoice.getBT0010BuyerReference().isEmpty()) {
            BT0010BuyerReference bt0010 = invoice.getBT0010BuyerReference(0);
            Element buyerReference = new Element("BuyerReference", ramNs);
            buyerReference.setText(bt0010.getValue());
            applicableHeaderTradeAgreement.addContent(buyerReference);
        }

        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer bg0007 = invoice.getBG0007Buyer(0);
            Element buyerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "BuyerTradeParty");
            if (buyerTradeParty == null) {
                buyerTradeParty = new Element("BuyerTradeParty", ramNs);
                applicableHeaderTradeAgreement.addContent(buyerTradeParty);
            }

            if (!bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0046 = bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs); // maybe GlobalID ?
                id.setText(bt0046.toString());
                if (bt0046.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", "");
                }
                buyerTradeParty.addContent(id);
            }

            if (!bg0007.getBT0044BuyerName().isEmpty()) {
                BT0044BuyerName bt0044 = bg0007.getBT0044BuyerName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0044.getValue());
                buyerTradeParty.addContent(name);
            } else if (!bg0007.getBG0009BuyerContact().isEmpty()) {
                BG0009BuyerContact bg0009 = bg0007.getBG0009BuyerContact(0);
                if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                    BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                    Element name = new Element("Name", ramNs);
                    name.setText(bt0056.getValue());
                    buyerTradeParty.addContent(name);
                }
            }

            Element specifiedLegalOrganization = new Element("SpecifiedLegalOrganization", ramNs);
            buyerTradeParty.addContent(specifiedLegalOrganization);

            if (!bg0007.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0047 = bg0007.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs);
                id.setText(bt0047.getIdentifier());
                if (bt0047.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", bt0047.getIdentificationSchema());
                }
                specifiedLegalOrganization.addContent(id);
            }

            if (!bg0007.getBT0045BuyerTradingName().isEmpty()) {
                BT0045BuyerTradingName bt0045 = bg0007.getBT0045BuyerTradingName(0);
                Element tradingBusinessName = new Element("TradingBusinessName", ramNs);
                tradingBusinessName.setText(bt0045.getValue());
                specifiedLegalOrganization.addContent(tradingBusinessName);
            }

            if (!bg0007.getBG0009BuyerContact().isEmpty()) {
                BG0009BuyerContact bg0009 = bg0007.getBG0009BuyerContact(0);
                Element definedTradeContact = new Element("DefinedTradeContact", ramNs);
                buyerTradeParty.addContent(definedTradeContact);

                if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                    BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                    Element personName = new Element("PersonName", ramNs);
                    personName.setText(bt0056.getValue());
                    definedTradeContact.addContent(personName);
                }

                if (!bg0009.getBT0057BuyerContactTelephoneNumber().isEmpty()) {
                    BT0057BuyerContactTelephoneNumber bt0057 = bg0009.getBT0057BuyerContactTelephoneNumber(0);
                    Element telephoneUniversalCommunication = new Element("TelephoneUniversalCommunication", ramNs);
                    Element completeNumber = new Element("CompleteNumber", ramNs);
                    completeNumber.setText(bt0057.getValue());
                    telephoneUniversalCommunication.addContent(completeNumber);
                    definedTradeContact.addContent(telephoneUniversalCommunication);
                }

                if (!bg0009.getBT0058BuyerContactEmailAddress().isEmpty()) {
                    BT0058BuyerContactEmailAddress bt0058 = bg0009.getBT0058BuyerContactEmailAddress(0);
                    Element emailURIUniversalCommunication = new Element("EmailURIUniversalCommunication", ramNs);
                    Element uriid = new Element("URIID", ramNs);
                    uriid.setText(bt0058.getValue());
                    emailURIUniversalCommunication.addContent(uriid);
                    definedTradeContact.addContent(emailURIUniversalCommunication);
                }
            }

            if (!bg0007.getBG0008BuyerPostalAddress().isEmpty()) {
                BG0008BuyerPostalAddress bg0008 = bg0007.getBG0008BuyerPostalAddress(0);
                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                buyerTradeParty.addContent(postalTradeAddress);

                if (!bg0008.getBT0053BuyerPostCode().isEmpty()) {
                    BT0053BuyerPostCode bt0053 = bg0008.getBT0053BuyerPostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0053.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if (!bg0008.getBT0050BuyerAddressLine1().isEmpty()) {
                    BT0050BuyerAddressLine1 bt0050 = bg0008.getBT0050BuyerAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0050.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if (!bg0008.getBT0051BuyerAddressLine2().isEmpty()) {
                    BT0051BuyerAddressLine2 bt0051 = bg0008.getBT0051BuyerAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0051.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if (!bg0008.getBT0163BuyerAddressLine3().isEmpty()) {
                    BT0163BuyerAddressLine3 bt0163 = bg0008.getBT0163BuyerAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0163.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if (!bg0008.getBT0052BuyerCity().isEmpty()) {
                    BT0052BuyerCity bt0052 = bg0008.getBT0052BuyerCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0052.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if (!bg0008.getBT0055BuyerCountryCode().isEmpty()) {
                    BT0055BuyerCountryCode bt0055 = bg0008.getBT0055BuyerCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0055.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }

                if (!bg0008.getBT0054BuyerCountrySubdivision().isEmpty()) {
                    BT0054BuyerCountrySubdivision bt0054 = bg0008.getBT0054BuyerCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0054.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }
            }

            if (!bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                Identifier bt0049 = bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0).getValue();
                Element uriUniversalCommunication = new Element("URIUniversalCommunication", ramNs);
                Element uriid = new Element("URIID", ramNs);
                uriid.setText(bt0049.getIdentifier());
                if (bt0049.getIdentificationSchema() != null) {
                    uriid.setAttribute("schemeID", bt0049.getIdentificationSchema());
                }
                uriUniversalCommunication.addContent(uriid);
                buyerTradeParty.addContent(uriUniversalCommunication);
            }

            if (!bg0007.getBT0048BuyerVatIdentifier().isEmpty()) {
                Identifier bt0048 = bg0007.getBT0048BuyerVatIdentifier(0).getValue();
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                Element id = new Element("ID", ramNs);
                String identifier = bt0048.getIdentifier();
                String schema = bt0048.getIdentificationSchema();
                if (schema != null) {
                    id.setText(String.format("%s %s", schema, identifier));
                } else {
                    id.setText(identifier);
                }
                id.setAttribute("schemeID", "VAT");

                specifiedTaxRegistration.addContent(id);
                buyerTradeParty.addContent(specifiedTaxRegistration);
            }

        }
    }
}

