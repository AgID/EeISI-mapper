package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
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
                BG0007Buyer buyer = cenInvoice.getBG0007Buyer(0);
                if (!buyer.getBT0048BuyerVatIdentifier().isEmpty() || !buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {

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

                    if (!buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                        BT0049BuyerElectronicAddressAndSchemeIdentifier bt0049 = buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0);
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


                    if (!buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                        final Identifier identifier = buyer.getBT0046BuyerIdentifierAndSchemeIdentifier(0).getValue();
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
                        id.setText(String.format("%s:%s", schema, ide));
//                        if (schema != null) id.setAttribute("schemeID", schema);
                    }


                    if (!buyer.getBT0048BuyerVatIdentifier().isEmpty()) {
                        BT0048BuyerVatIdentifier buyerVatIdentifier = buyer.getBT0048BuyerVatIdentifier(0);
                        Element companyID = new Element("CompanyID");
                        Identifier identifier = buyerVatIdentifier.getValue();
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
                        Element taxScheme = new Element("TaxScheme");
                        Element taxSchemeId = new Element("ID");
                        taxSchemeId.setText("VAT");
                        taxScheme.addContent(taxSchemeId);
                        partyTaxScheme.addContent(taxScheme);

                    }

                    Element partyLegalEntity = party.getChild("PartyLegalEntity");
                    if (partyLegalEntity == null) {
                        partyLegalEntity = new Element("PartyLegalEntity");
                        party.addContent(partyLegalEntity);
                    }

                    Element registrationName = new Element("RegistrationName");
                    if (!buyer.getBG0009BuyerContact().isEmpty()) {
                        BG0009BuyerContact bg0009 = buyer.getBG0009BuyerContact(0);
                        if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                            BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                            registrationName.setText(bt0056.getValue());
                            partyLegalEntity.addContent(registrationName);
                        }
                    } else if (!buyer.getBT0044BuyerName().isEmpty()) {
                        BT0044BuyerName bt0044 = buyer.getBT0044BuyerName(0);
                        registrationName.setText(bt0044.getValue());
                        partyLegalEntity.addContent(registrationName);
                    }

                }
            }
        }
    }
}