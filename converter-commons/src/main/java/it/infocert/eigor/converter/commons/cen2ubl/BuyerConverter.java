package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuyerConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(BuyerConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        final Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0007Buyer().isEmpty()) {
                BG0007Buyer bg0007 = cenInvoice.getBG0007Buyer(0);
                if (!bg0007.getBT0048BuyerVatIdentifier().isEmpty() || !bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {

                    final Element accountingCustomerParty = Optional.fromNullable(root.getChild("AccountingCustomerParty")).or(new Supplier<Element>() {
                        @Override
                        public Element get() {
                            final Element a = new Element("AccountingCustomerParty");
                            root.addContent(a);
                            return a;
                        }
                    });

                    final Element party = Optional.fromNullable(accountingCustomerParty.getChild("Party")).or(new Supplier<Element>() {
                        @Override
                        public Element get() {
                            final Element p = new Element("Party");
                            accountingCustomerParty.addContent(p);
                            return p;
                        }
                    });

                    if (!bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                        BT0049BuyerElectronicAddressAndSchemeIdentifier bt0049 = bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0);
                        Element endpointID = new Element("EndpointID");
                        Identifier identifier = bt0049.getValue();
                        if (identifier.getIdentificationSchema() != null) {
                            endpointID.setAttribute("schemeID", identifier.getIdentificationSchema());
                        }
                        if (identifier.getIdentifier() != null) {
                            endpointID.addContent(identifier.getIdentifier());
                        }
                        party.addContent(endpointID);
                    }


                    if (!bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                        final Identifier identifier = bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier(0).getValue();
                        final Element partyIdentification = Optional.fromNullable(party.getChild("PartyIdentification")).or(new Supplier<Element>() {
                            @Override
                            public Element get() {
                                final Element p = new Element("PartyIdentification");
                                party.addContent(p);
                                return p;
                            }
                        });

                        final Element id = Optional.fromNullable(partyIdentification.getChild("ID")).or(new Supplier<Element>() {
                            @Override
                            public Element get() {
                                final Element i = new Element("ID");
                                partyIdentification.addContent(i);
                                return i;
                            }
                        });

                        final String ide = identifier.getIdentifier();
                        final String schema = identifier.getIdentificationSchema();
                        id.setText(ide);
                        if (schema != null) id.setAttribute("schemeID", schema);
                    }

                    if (!bg0007.getBG0008BuyerPostalAddress().isEmpty()) {
                        final BG0008BuyerPostalAddress bg0008 = bg0007.getBG0008BuyerPostalAddress(0);

                        Element postalAddress = party.getChild("PostalAddress");
                        if (postalAddress == null) {
                            postalAddress = new Element("PostalAddress");
                            party.addContent(postalAddress);
                        }

                        if (!bg0008.getBT0050BuyerAddressLine1().isEmpty()) {
                            BT0050BuyerAddressLine1 bt0050 = bg0008.getBT0050BuyerAddressLine1(0);
                            Element streetName = new Element("StreetName");
                            streetName.setText(bt0050.getValue());
                            postalAddress.addContent(streetName);
                        }

                        if (!bg0008.getBT0052BuyerCity().isEmpty()) {
                            BT0052BuyerCity bt0052 = bg0008.getBT0052BuyerCity(0);
                            Element cityName = new Element("CityName");
                            cityName.setText(bt0052.getValue());
                            postalAddress.addContent(cityName);
                        }

                        if (!bg0008.getBT0053BuyerPostCode().isEmpty()) {
                            BT0053BuyerPostCode bt0053 = bg0008.getBT0053BuyerPostCode(0);
                            Element postalZone = new Element("PostalZone");
                            postalZone.setText(bt0053.getValue());
                            postalAddress.addContent(postalZone);
                        }

                        if (!bg0008.getBT0055BuyerCountryCode().isEmpty()) {
                            BT0055BuyerCountryCode bt0055 = bg0008.getBT0055BuyerCountryCode(0);
                            Element country = postalAddress.getChild("Country");
                            if (country == null) {
                                country = new Element("Country");
                                postalAddress.addContent(country);
                            }
                            Element identificationCode = new Element("IdentificationCode");
                            Iso31661CountryCodes code = bt0055.getValue();
                            identificationCode.setText(code.name());
                            country.addContent(identificationCode);
                        }
                    }

                    if (!bg0007.getBT0048BuyerVatIdentifier().isEmpty()) {
                        BT0048BuyerVatIdentifier bt0048 = bg0007.getBT0048BuyerVatIdentifier(0);
                        Element companyID = new Element("CompanyID");
                        Identifier identifier = bt0048.getValue();
//                        if (identifier.getIdentificationSchema() != null) {
//                            companyID.setAttribute("schemeID", identifier.getIdentificationSchema());
//                        }
//                        if (identifier.getIdentifier() != null) {
//                            companyID.addContent(identifier.getIdentifier());
//                        }
                        String companyIDValue = "";
                        if (identifier.getIdentificationSchema() != null) {
                            companyIDValue += identifier.getIdentificationSchema();
                        }
                        if (identifier.getIdentifier() != null) {
                            companyIDValue += identifier.getIdentifier();
                        }
                        companyID.addContent(companyIDValue);
                        Element partyTaxScheme = party.getChild("PartyTaxScheme");
                        if (partyTaxScheme == null) {
                            partyTaxScheme = new Element("PartyTaxScheme");
                            party.addContent(partyTaxScheme);
                        }
                        partyTaxScheme.addContent(companyID);
                    }


                    Element registrationName = null;

                    if (!bg0007.getBG0009BuyerContact().isEmpty()) {
                        BG0009BuyerContact bg0009 = bg0007.getBG0009BuyerContact(0);
                        if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                            BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                            registrationName = new Element("RegistrationName");
                            registrationName.setText(bt0056.getValue());
                        }
                    } else if (!bg0007.getBT0044BuyerName().isEmpty()) {
                        BT0044BuyerName bt0044 = bg0007.getBT0044BuyerName(0);
                        registrationName = new Element("RegistrationName");
                        registrationName.setText(bt0044.getValue());

                    }

                    if (registrationName != null) {
                        Element partyLegalEntity = party.getChild("PartyLegalEntity");
                        if (partyLegalEntity == null) {
                            partyLegalEntity = new Element("PartyLegalEntity");
                            party.addContent(partyLegalEntity);
                        }
                        partyLegalEntity.addContent(registrationName);
                    }
                }
            }
        }
    }
}