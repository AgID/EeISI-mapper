package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0005SellerPostalAddress;
import it.infocert.eigor.model.core.model.BG0006SellerContact;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class AccountSupplierPartyConverter implements CustomMapping<Document> {

    private final String SUPPLIER = "AccountingSupplierParty";
    private final String PARTY = "Party";


    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        final Element root = document.getRootElement();
        final Element party;
        final Element supplier = root.getChild(SUPPLIER);
        if (supplier == null) {
            Element s = new Element(SUPPLIER);
            party = new Element(PARTY);
            s.addContent(party);
            root.addContent(s);
        } else {
            party = supplier.getChild(PARTY);
        }


        if (invoice.getBG0004Seller().isEmpty()) {
            return;
        }

        BG0004Seller seller = invoice.getBG0004Seller(0);

        if (!seller.getBG0005SellerPostalAddress().isEmpty()) {
            BG0005SellerPostalAddress sellerPostalAddress = seller.getBG0005SellerPostalAddress(0);
            Element postalAddress = new Element("PostalAddress");
            party.addContent(postalAddress);
            if (!sellerPostalAddress.getBT0035SellerAddressLine1().isEmpty()) {
                Element streetName = new Element("StreetName");
                streetName.setText(sellerPostalAddress.getBT0035SellerAddressLine1(0).getValue());
                postalAddress.addContent(streetName);
            }

            if (!sellerPostalAddress.getBT0037SellerCity().isEmpty()) {
                Element cityName = new Element("CityName");
                cityName.setText(sellerPostalAddress.getBT0037SellerCity(0).getValue());
                postalAddress.addContent(cityName);
            }

            if (!sellerPostalAddress.getBT0038SellerPostCode().isEmpty()) {
                Element postalZone = new Element("PostalZone");
                postalZone.setText(sellerPostalAddress.getBT0038SellerPostCode(0).getValue());
                postalAddress.addContent(postalZone);
            }

            if (!sellerPostalAddress.getBT0040SellerCountryCode().isEmpty()) {
                Element identificationCode = new Element("IdentificationCode");
                Element country = new Element("Country").addContent(identificationCode);
                identificationCode.setText(sellerPostalAddress.getBT0040SellerCountryCode(0).getValue().getIso2charCode());
                postalAddress.addContent(country);
            }
        }


        if (!seller.getBT0031SellerVatIdentifier().isEmpty() || !seller.getBT0032SellerTaxRegistrationIdentifier().isEmpty()) {
            Element partyTaxScheme = new Element("PartyTaxScheme");
            Element companyID = new Element("CompanyID");
            Element taxScheme = new Element("TaxScheme");
            Element taxSchemeId = new Element("ID");
            String companyIdValue;
            String taxSchemeIdValue;
            if (!seller.getBT0031SellerVatIdentifier().isEmpty()) {
                companyIdValue = seller.getBT0031SellerVatIdentifier(0).getValue();
                taxSchemeIdValue = "VAT";
            } else {
                companyIdValue = seller.getBT0032SellerTaxRegistrationIdentifier(0).getValue();
                taxSchemeIdValue = "NoVAT";

            }

            taxSchemeId.setText(taxSchemeIdValue);
            companyID.setText(companyIdValue);
            taxScheme.addContent(taxSchemeId);
            partyTaxScheme.addContent(companyID);
            partyTaxScheme.addContent(taxScheme);

            party.addContent(partyTaxScheme);
        }

        Element partyLegalEntity = new Element("PartyLegalEntity");
        party.addContent(partyLegalEntity);

        if (!seller.getBT0027SellerName().isEmpty()) {
            Element registrationName = new Element("RegistrationName");
            registrationName.setText(seller.getBT0027SellerName(0).getValue());
            partyLegalEntity.addContent(registrationName);
        }

        if (!seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
            Element companyID = new Element("CompanyID");
            Identifier id = seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
            companyID.setText(id.getIdentifier());
            if (id.getIdentificationSchema() != null) {
                companyID.setAttribute("schemeID", id.getIdentificationSchema());
            }
            partyLegalEntity.addContent(companyID);
        }

        if (!seller.getBG0006SellerContact().isEmpty()) {
            BG0006SellerContact sellerContact = seller.getBG0006SellerContact(0);
            Element contact = new Element("Contact");
            party.addContent(contact);

            if (!sellerContact.getBT0042SellerContactTelephoneNumber().isEmpty()) {
                Element telephone = new Element("Telephone");
                telephone.setText(sellerContact.getBT0042SellerContactTelephoneNumber(0).getValue());
                contact.addContent(telephone);
            }

            if (!sellerContact.getBT0043SellerContactEmailAddress().isEmpty()) {
                Element electronicMail = new Element("ElectronicMail");
                electronicMail.setText(sellerContact.getBT0043SellerContactEmailAddress(0).getValue());
                contact.addContent(electronicMail);
            }
        }


    }
    /*
     */
}
