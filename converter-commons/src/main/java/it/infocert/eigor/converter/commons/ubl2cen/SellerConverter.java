package it.infocert.eigor.converter.commons.ubl2cen;

import com.google.common.base.Optional;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {
    private final static Logger log = LoggerFactory.getLogger(SellerConverter.class);

    public ConversionResult<BG0000Invoice> toBT0029_31_32(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        BT0029SellerIdentifierAndSchemeIdentifier bt0029;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        if (invoice.getBG0004Seller().isEmpty()) {
            invoice.getBG0004Seller().add(new BG0004Seller());
            log.error("ADDED NEW SELLER");
        } else {
            log.error("SELLER ALREADY PRESENT");
        }

        Element accountingSupplierParty = findNamespaceChild(rootElement, namespacesInScope, "AccountingSupplierParty");

        if (accountingSupplierParty != null) {
            Element party = findNamespaceChild(accountingSupplierParty, namespacesInScope, "Party");

            if (party != null) {
                List<Element> partyIdentifications = findNamespaceChildren(party, namespacesInScope, "PartyIdentification");

                for (Element elemParty : partyIdentifications) {

                    Element id = findNamespaceChild(elemParty, namespacesInScope, "ID");
                    if (id != null) {
                        bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), id.getText()));
                        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                    }
                }

                //BT0031-BT0032
                List<Element> partyTaxScheme = findNamespaceChildren(party, namespacesInScope, "PartyTaxScheme");
                String idValue = null;
                BT0031SellerVatIdentifier bt0031;
                BT0032SellerTaxRegistrationIdentifier bt0032;
                for (Element elemPartyTax : partyTaxScheme) {
                    Element taxScheme = findNamespaceChild(elemPartyTax, namespacesInScope, "TaxScheme");
                    if (taxScheme != null) {
                        Element id = findNamespaceChild(taxScheme, namespacesInScope, "ID");
                        if (id != null) {
                            idValue = id.getText();
                        }
                    }
                    Element companyID = findNamespaceChild(elemPartyTax, namespacesInScope, "CompanyID");
                    if (companyID != null && idValue != null) {
                        if (idValue.equals("VAT")) {
                            bt0031 = new BT0031SellerVatIdentifier(companyID.getText());
                            invoice.getBG0004Seller(0).getBT0031SellerVatIdentifier().add(bt0031);
                        } else {
                            bt0032 = new BT0032SellerTaxRegistrationIdentifier(companyID.getText());
                            invoice.getBG0004Seller(0).getBT0032SellerTaxRegistrationIdentifier().add(bt0032);
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
//    /Invoice/cac:PayeeParty/cac:PartyIdentification/cbc:ID
//    /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBT0029_31_32(document, cenInvoice, errors, callingLocation);
        mapBankIdentifier(cenInvoice, document);
    }

    private void mapBankIdentifier(final BG0000Invoice invoice, final Document document) {
        final Element root = document.getRootElement();
        final List<Namespace> namespacesInScope = root.getNamespacesIntroduced();
        final Optional<Element> payeeParty = Optional.fromNullable(findNamespaceChild(root, namespacesInScope, "PayeeParty"));
        if (payeeParty.isPresent()) {
            final Optional<Element> partyIdentification = Optional.fromNullable(findNamespaceChild(payeeParty.get(), namespacesInScope, "PartyIdentification"));
            if (partyIdentification.isPresent()) {
                final Optional<Element> idOptional = Optional.fromNullable(findNamespaceChild(partyIdentification.get(), namespacesInScope, "ID"));
                if (idOptional.isPresent()) {
                    mapId(invoice, idOptional.get());
                } else
                    log("ID");
            } else {
                log("PartyIdentification");
            }
        } else {
            log("PayeeParty");
        }
        final Optional<Element> accountingSupplierParty = Optional.fromNullable(findNamespaceChild(root, namespacesInScope, "AccountingSupplierParty"));
        if (accountingSupplierParty.isPresent()) {
            final Optional<Element> party = Optional.fromNullable(findNamespaceChild(accountingSupplierParty.get(), namespacesInScope, "Party"));
            if (party.isPresent()) {
                final Optional<Element> partyIdentification = Optional.fromNullable(findNamespaceChild(party.get(), namespacesInScope, "PartyIdentification"));
                if (partyIdentification.isPresent()) {
                    final Optional<Element> idOptional = Optional.fromNullable(findNamespaceChild(partyIdentification.get(), namespacesInScope, "ID"));
                    if (idOptional.isPresent()) {
                        mapId(invoice, idOptional.get());
                    } else
                        log("ID");
                } else {
                    log("PartyIdentification");
                }
            } else {
                log("Party");
            }
        } else {
            log("AccountingSupplierParty");
        }

    }

    private void mapId(BG0000Invoice invoice, Element id) {
        final Attribute sepa = id.getAttribute("schemeID");
        if (sepa != null && "sepa".equalsIgnoreCase(sepa.getValue())) {
            final Identifier identifier = new Identifier(sepa.getValue(), id.getText());
            final BG0016PaymentInstructions bg16 = new BG0016PaymentInstructions();
            final BG0019DirectDebit bg19 = new BG0019DirectDebit();
            bg19.getBT0090BankAssignedCreditorIdentifier().add(new BT0090BankAssignedCreditorIdentifier(identifier));
            bg16.getBG0019DirectDebit().add(bg19);
            invoice.getBG0016PaymentInstructions().add(bg16);
        } else
            log.info("Element 'ID' has no schemeID or it is not of value 'SEPA'. SchemeID: {}", sepa != null ? sepa.getValue() : "null");
    }


    private void log(final String item) {
        log.info("No {} in current UBL invoice", item);
    }
}
