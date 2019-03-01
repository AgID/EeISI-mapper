package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class AccountSupplierPartyConverter implements CustomMapping<Document> {

	private final static Logger logger = LoggerFactory.getLogger(AccountSupplierPartyConverter.class);

	private final String SUPPLIER = "AccountingSupplierParty";
	private final String PARTY = "Party";
	private final String Endpoint = "EndpointID";


	@Override
	public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

		final Element root = document.getRootElement();
		final Element partyElm;
		final Element supplier = root.getChild(SUPPLIER);
		Element accountSupplierPartyElm = new Element(SUPPLIER);
		if (supplier == null) {
			Element s = new Element(SUPPLIER);
			partyElm = new Element(PARTY);
			s.addContent(partyElm);
			root.addContent(s);
		} else {
			partyElm = supplier.getChild(PARTY);
		}


		if (invoice.getBG0004Seller().isEmpty()) {
			return;
		}
		
		
		
		

		BG0004Seller seller = invoice.getBG0004Seller(0);
		
		String identifierText;
		String identificationSchemaStr;
		if (seller.getBT0034SellerElectronicAddressAndSchemeIdentifier().isEmpty()) {
			identifierText = "NA";

			identificationSchemaStr = "9921";
		} else {
			BT0034SellerElectronicAddressAndSchemeIdentifier bt34 = seller.getBT0034SellerElectronicAddressAndSchemeIdentifier(0);
			identifierText = bt34.getValue().getIdentifier();
			identificationSchemaStr = bt34.getValue().getIdentificationSchema();
		}

		Element endpointElm = new Element(Endpoint);
		endpointElm.setText(identifierText);
		endpointElm.setAttribute("schemeID",identificationSchemaStr);
		partyElm.addContent(endpointElm);
		
		if (!seller.getBG0005SellerPostalAddress().isEmpty()) {
			BG0005SellerPostalAddress sellerPostalAddress = seller.getBG0005SellerPostalAddress(0);
			Element postalAddress = new Element("PostalAddress");
			partyElm.addContent(postalAddress);
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


		if (!seller.getBT0031SellerVatIdentifier().isEmpty()) {
			mapPartyTaxScheme(partyElm, seller.getBT0031SellerVatIdentifier(0).getValue(), "NoVAT");
		} else {
			logger.debug("BT-31 is missing");
		}
		if (!seller.getBT0032SellerTaxRegistrationIdentifier().isEmpty()) {
			mapPartyTaxScheme(partyElm, seller.getBT0032SellerTaxRegistrationIdentifier(0).getValue(), "VAT");
		} else {
			logger.debug("BT-31 is missing");
		}

		Element partyLegalEntity = new Element("PartyLegalEntity");
		partyElm.addContent(partyLegalEntity);

		if (!seller.getBT0027SellerName().isEmpty()) {
			Element registrationName = new Element("RegistrationName");
			registrationName.setText(seller.getBT0027SellerName(0).getValue());
			partyLegalEntity.addContent(registrationName);
		}

		if (!seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {

			Identifier id = seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
			String identificationSchema = id.getIdentificationSchema();

			// Italy haven't yet registered their schemas, so in this case,
			// the schemas has to be included directly in the value
			if(identificationSchema!=null && identificationSchema.startsWith("IT:")){

				StringBuilder companyIdBuffer = new StringBuilder();
				if (identificationSchema != null) {
					companyIdBuffer.append(identificationSchema).append(":");
				}
				companyIdBuffer.append(id.getIdentifier());

				Element companyID = new Element("CompanyID");
				companyID.setText( companyIdBuffer.toString() );
				partyLegalEntity.addContent(companyID);

			}
			// For other countries, we'll keep on doing as before
			else{

				Element companyID = new Element("CompanyID");
				companyID.setText(id.getIdentifier());
				if (identificationSchema != null) {
					companyID.setAttribute("schemeID", identificationSchema);
				}
				partyLegalEntity.addContent(companyID);

			}

		}

		

		if (!seller.getBG0006SellerContact().isEmpty()) {
			BG0006SellerContact sellerContact = seller.getBG0006SellerContact(0);
			Element contact = new Element("Contact");
			partyElm.addContent(contact);

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

	private void mapPartyTaxScheme(Element party, String companyIdValue, String taxSchemeIdValue) {

		Element taxSchemeId = new Element("ID");
		taxSchemeId.setText(taxSchemeIdValue);

		Element companyID = new Element("CompanyID");
		companyID.setText(companyIdValue);

		Element taxScheme = new Element("TaxScheme");
		taxScheme.addContent(taxSchemeId);

		Element partyTaxScheme = new Element("PartyTaxScheme");
		partyTaxScheme.addContent(companyID);
		partyTaxScheme.addContent(taxScheme);

		party.addContent(partyTaxScheme);
	}

}
