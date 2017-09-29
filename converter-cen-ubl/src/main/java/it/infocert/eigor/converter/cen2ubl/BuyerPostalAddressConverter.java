package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuyerPostalAddressConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(BuyerPostalAddressConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0007Buyer().isEmpty()) {
                BG0007Buyer bg0007 = cenInvoice.getBG0007Buyer(0);
                if (!bg0007.getBG0008BuyerPostalAddress().isEmpty()) {
                    BG0008BuyerPostalAddress bg0008 = bg0007.getBG0008BuyerPostalAddress(0);

                    Element accountingCustomerParty = root.getChild("AccountingCustomerParty");
                    if (accountingCustomerParty == null) {
                        accountingCustomerParty = new Element("AccountingCustomerParty");
                        root.addContent(accountingCustomerParty);
                    }
                    Element party = accountingCustomerParty.getChild("Party");
                    if (party == null) {
                        party = new Element("Party");
                        accountingCustomerParty.addContent(party);
                    }
                    Element postalAddress = party.getChild("PostalAddress");
                    if (postalAddress == null) {
                        postalAddress = new Element("PostalAddress");
                        party.addContent(postalAddress);
                    }

                    if (!bg0008.getBT0050BuyerAddressLine1().isEmpty()) {
                        BT0050BuyerAddressLine1 bt0050 = bg0008.getBT0050BuyerAddressLine1(0);
                        if (bt0050 != null) {
                            Element streetName = new Element("StreetName");
                            streetName.addContent(bt0050.getValue());
                            postalAddress.addContent(streetName);
                        }
                    }

                    if (!bg0008.getBT0052BuyerCity().isEmpty()) {
                        BT0052BuyerCity bt0052 = bg0008.getBT0052BuyerCity(0);
                        if (bt0052 != null) {
                            Element cityName = new Element("CityName");
                            cityName.addContent(bt0052.getValue());
                            postalAddress.addContent(cityName);
                        }
                    }

                    if (!bg0008.getBT0053BuyerPostCode().isEmpty()) {
                        BT0053BuyerPostCode bt0053 = bg0008.getBT0053BuyerPostCode(0);
                        if (bt0053 != null) {
                            Element postalZone = new Element("PostalZone");
                            postalZone.addContent(bt0053.getValue());
                            postalAddress.addContent(postalZone);
                        }
                    }

                    if (!bg0008.getBT0055BuyerCountryCode().isEmpty()) {
                        BT0055BuyerCountryCode bt0055 = bg0008.getBT0055BuyerCountryCode(0);
                        if (bt0055 != null) {
                            Element country = postalAddress.getChild("Country");
                            if (country == null) {
                                country = new Element("Country");
                                postalAddress.addContent(country);
                            }
                            Element identificationCode = new Element("IdentificationCode");
                            Iso31661CountryCodes code = bt0055.getValue();
                            identificationCode.addContent(code.name());
                            country.addContent(identificationCode);
                        }
                    }
                }
            }
        }
    }
}