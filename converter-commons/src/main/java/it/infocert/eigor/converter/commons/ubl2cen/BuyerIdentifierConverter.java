package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0046BuyerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0048BuyerVatIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Buyer Identifier Custom Converter
 */
public class BuyerIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    private ConversionResult<BG0000Invoice> toBT0046_BT0048(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        BT0046BuyerIdentifierAndSchemeIdentifier bt0046;
        BT0048BuyerVatIdentifier bt0048;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element accountingCustomerParty = findNamespaceChild(rootElement, namespacesInScope, "AccountingCustomerParty");

        if (accountingCustomerParty != null) {
            Element party = findNamespaceChild(accountingCustomerParty, namespacesInScope, "Party");

            if (party != null) {
                Element partyIdentification = findNamespaceChild(party, namespacesInScope, "PartyIdentification");

                if (partyIdentification != null) {
                    Element id = findNamespaceChild(partyIdentification, namespacesInScope, "ID");

                    if (id != null) {
                        bt0046 = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), id.getText()));
                        if (!invoice.getBG0007Buyer().isEmpty()) {
                            invoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier().add(bt0046);
                        }
                    }
                }

                Element partyTaxScheme = findNamespaceChild(party, namespacesInScope, "PartyTaxScheme");
                if (partyTaxScheme != null) {
                    Element companyID = findNamespaceChild(partyTaxScheme, namespacesInScope, "CompanyID");
                    if (companyID != null) {
                        bt0048 = new BT0048BuyerVatIdentifier(new Identifier(companyID.getAttributeValue("schemeID"), companyID.getText()));
                        invoice.getBG0007Buyer(0).getBT0048BuyerVatIdentifier().add(bt0048);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBT0046_BT0048(document, cenInvoice, errors, callingLocation);
    }
}
